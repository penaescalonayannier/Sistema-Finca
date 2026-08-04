package com.kynsoft.report.applications.query.reporte.consolidadoPorResponsable;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.ReporteConsolidadoPorResponsableResponse;
import com.kynsoft.report.applications.query.responseObject.TrabajadorConsolidadoResponse;
import com.kynsoft.report.domain.dto.ResponsableConsolidadoDto;
import com.kynsoft.report.domain.dto.TrabajadorConsolidadoDto;
import com.kynsoft.report.infrastructure.entity.DiaTrabajo;
import com.kynsoft.report.infrastructure.entity.TrabajadorDia;
import com.kynsoft.report.infrastructure.repository.query.DiaTrabajoReadDataJPARepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GetReporteConsolidadoPorResponsableQueryHandler
        implements IQueryHandler<GetReporteConsolidadoPorResponsableQuery, ReporteConsolidadoPorResponsableResponse> {

    private final DiaTrabajoReadDataJPARepository diaTrabajoRepository;

    @Override
    public ReporteConsolidadoPorResponsableResponse handle(GetReporteConsolidadoPorResponsableQuery query) {
        String year = query.getYear();
        String mes = query.getMes();

        // Obtener todos los días del mes con sus trabajadores
        List<DiaTrabajo> dias = diaTrabajoRepository.findByYearAndMesWithTrabajadores(year, mes);

        if (dias.isEmpty()) {
            return new ReporteConsolidadoPorResponsableResponse(
                    year,
                    mes,
                    Collections.emptyList()
            );
        }

        // Determinar los días del mes
        int daysInMonth = YearMonth.of(Integer.parseInt(year), getMonthNumber(mes)).lengthOfMonth();

        // Mapa para agrupar por responsable: responsableId -> { trabajadorId -> Datos del trabajador }
        Map<String, Map<String, TrabajadorData>> responsablesTrabajadoresMap = new HashMap<>();

        // Procesar cada día
        for (DiaTrabajo dia : dias) {
            int diaDelMes = dia.getFecha().getDayOfMonth();

            // Procesar cada trabajador del día
            for (TrabajadorDia td : dia.getTrabajadores()) {
                String responsableId = dia.getReporte().getTrabajadorResponsableId() != null
                        ? dia.getReporte().getTrabajadorResponsableId().toString()
                        : "SIN_RESPONSABLE";
                String responsableNombre = dia.getReporte().getTrabajadorResponsable() != null
                        ? dia.getReporte().getTrabajadorResponsable().getNombre()
                        : "Sin asignar";

                String trabajadorId = td.getTrabajador().getId().toString();
                String nombre = td.getTrabajador().getNombre();
                String ruc = td.getTrabajador().getRuc();
                String cargo = td.getTrabajador().getCargo() != null ? td.getTrabajador().getCargo().getName() : null;
                String cuenta = td.getTrabajador().getCuenta();
                String horas = td.getHoras() != null ? td.getHoras() : "";

                // Obtener o crear el mapa de trabajadores para este responsable
                Map<String, TrabajadorData> trabajadoresMap = responsablesTrabajadoresMap
                        .computeIfAbsent(responsableId, k -> new HashMap<>());

                // Obtener o crear el registro del trabajador
                TrabajadorData data = trabajadoresMap.computeIfAbsent(
                        trabajadorId,
                        k -> new TrabajadorData(trabajadorId, nombre, ruc, cargo, cuenta, responsableId, responsableNombre, daysInMonth)
                );

                // Agregar horas al día correspondiente
                if (horas != null && !horas.isEmpty()) {
                    int horasNum = convertirHorasANumero(horas);

                    // Si ya hay horas para este día, sumarlas
                    String horasActuales = data.horasPorDia.get(diaDelMes);
                    if (horasActuales != null && !horasActuales.isEmpty()) {
                        int horasAnteriores = convertirHorasANumero(horasActuales);
                        int horasTotales = horasAnteriores + horasNum;
                        data.horasPorDia.put(diaDelMes, String.valueOf(horasTotales));
                    } else {
                        data.horasPorDia.put(diaDelMes, horas);
                    }
                }
            }
        }

        // Convertir a DTOs
        List<ResponsableConsolidadoDto> responsablesList = new ArrayList<>();

        for (Map.Entry<String, Map<String, TrabajadorData>> responsableEntry : responsablesTrabajadoresMap.entrySet()) {
            String responsableId = responsableEntry.getKey();
            Map<String, TrabajadorData> trabajadoresMap = responsableEntry.getValue();

            List<TrabajadorConsolidadoDto> trabajadoresList = trabajadoresMap.values().stream()
                    .map(data -> TrabajadorConsolidadoDto.builder()
                            .trabajadorId(data.trabajadorId)
                            .nombre(data.nombre)
                            .ruc(data.ruc)
                            .cargo(data.cargo)
                            .cuenta(data.cuenta)
                            .horasPorDia(data.horasPorDia)
                            .totalHoras(calcularTotalHoras(data.horasPorDia))
                            .build())
                    .sorted((a, b) -> a.getNombre().compareTo(b.getNombre()))
                    .collect(Collectors.toList());

            if (!trabajadoresList.isEmpty()) {
                TrabajadorData firstWorker = trabajadoresMap.values().stream().findFirst().orElse(null);
                if (firstWorker != null) {
                    ResponsableConsolidadoDto responsable = ResponsableConsolidadoDto.builder()
                            .trabajadorResponsableId(java.util.UUID.fromString(responsableId.equals("SIN_RESPONSABLE") ? "00000000-0000-0000-0000-000000000000" : responsableId))
                            .trabajadorResponsableNombre(firstWorker.responsableNombre)
                            .trabajadores(trabajadoresList)
                            .build();
                    responsablesList.add(responsable);
                }
            }
        }

        // Ordenar responsables por nombre
        responsablesList.sort((a, b) -> {
            String nameA = a.getTrabajadorResponsableNombre() != null ? a.getTrabajadorResponsableNombre() : "";
            String nameB = b.getTrabajadorResponsableNombre() != null ? b.getTrabajadorResponsableNombre() : "";
            return nameA.compareTo(nameB);
        });

        return new ReporteConsolidadoPorResponsableResponse(year, mes, responsablesList);
    }

    private int getMonthNumber(String mes) {
        Map<String, Integer> meses = Map.ofEntries(
                Map.entry("Enero", 1), Map.entry("Febrero", 2), Map.entry("Marzo", 3),
                Map.entry("Abril", 4), Map.entry("Mayo", 5), Map.entry("Junio", 6),
                Map.entry("Julio", 7), Map.entry("Agosto", 8), Map.entry("Septiembre", 9),
                Map.entry("Octubre", 10), Map.entry("Noviembre", 11), Map.entry("Diciembre", 12)
        );
        return meses.getOrDefault(mes, 1);
    }

    private int convertirHorasANumero(String horas) {
        try {
            if (horas.contains(":")) {
                String[] partes = horas.split(":");
                int horasInt = Integer.parseInt(partes[0]);
                int minutosInt = partes.length > 1 ? Integer.parseInt(partes[1]) : 0;
                return horasInt + (minutosInt > 0 ? 1 : 0);
            } else {
                return Integer.parseInt(horas);
            }
        } catch (Exception e) {
            return 0;
        }
    }

    private double calcularTotalHoras(Map<Integer, String> horasPorDia) {
        return horasPorDia.values().stream()
                .mapToDouble(h -> {
                    try {
                        return Double.parseDouble(h);
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                })
                .sum();
    }

    private static class TrabajadorData {
        String trabajadorId;
        String nombre;
        String ruc;
        String cargo;
        String cuenta;
        String responsableId;
        String responsableNombre;
        Map<Integer, String> horasPorDia;

        TrabajadorData(String trabajadorId, String nombre, String ruc, String cargo, String cuenta, String responsableId, String responsableNombre, int daysInMonth) {
            this.trabajadorId = trabajadorId;
            this.nombre = nombre;
            this.ruc = ruc;
            this.cargo = cargo;
            this.cuenta = cuenta;
            this.responsableId = responsableId;
            this.responsableNombre = responsableNombre;
            this.horasPorDia = new HashMap<>();
            for (int i = 1; i <= daysInMonth; i++) {
                this.horasPorDia.put(i, "");
            }
        }
    }
}
