package com.kynsoft.report.applications.query.reporte.consolidado;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.ReporteConsolidadoResponse;
import com.kynsoft.report.applications.query.responseObject.TrabajadorConsolidadoResponse;
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
public class GetReporteConsolidadoQueryHandler
        implements IQueryHandler<GetReporteConsolidadoQuery, ReporteConsolidadoResponse> {

    private final DiaTrabajoReadDataJPARepository diaTrabajoRepository;

    @Override
    public ReporteConsolidadoResponse handle(GetReporteConsolidadoQuery query) {
        String year = query.getYear();
        String mes = query.getMes();

        // Obtener todos los días del mes con sus trabajadores
        List<DiaTrabajo> dias = diaTrabajoRepository.findByYearAndMesWithTrabajadores(year, mes);

        if (dias.isEmpty()) {
            return new ReporteConsolidadoResponse(
                    year,
                    mes,
                    Collections.emptyList()
            );
        }

        // Determinar los días del mes
        int daysInMonth = YearMonth.of(Integer.parseInt(year), getMonthNumber(mes)).lengthOfMonth();

        // Mapa para agrupar por trabajador: trabajadorId -> Datos del trabajador
        Map<String, TrabajadorData> trabajadorDataMap = new HashMap<>();

        // Procesar cada día
        for (DiaTrabajo dia : dias) {
            int diaDelMes = dia.getFecha().getDayOfMonth();

            // Procesar cada trabajador del día
            for (TrabajadorDia td : dia.getTrabajadores()) {
                String trabajadorId = td.getTrabajador().getId().toString();
                String nombre = td.getTrabajador().getNombre();
                String ruc = td.getTrabajador().getRuc();
                String cargo = td.getTrabajador().getCargo() != null ? td.getTrabajador().getCargo().getName() : null;
                String cuenta = td.getTrabajador().getCuenta();
                String horas = td.getHoras() != null ? td.getHoras() : "";

                // Obtener o crear el registro del trabajador
                TrabajadorData data = trabajadorDataMap.computeIfAbsent(
                        trabajadorId,
                        k -> new TrabajadorData(trabajadorId, nombre, ruc, cargo, cuenta, daysInMonth)
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
                        data.totalHoras += horasNum;
                    } else {
                        data.horasPorDia.put(diaDelMes, horas);
                        data.totalHoras += horasNum;
                    }
                }
            }
        }

        // Convertir a respuesta
        List<TrabajadorConsolidadoResponse> trabajadores = trabajadorDataMap.values().stream()
                .map(data -> new TrabajadorConsolidadoResponse(
                data.trabajadorId,
                data.nombre,
                data.ruc,
                data.cargo,
                data.cuenta,
                data.horasPorDia,
                data.totalHoras
        ))
                .sorted(Comparator.comparing(TrabajadorConsolidadoResponse::getNombre))
                .collect(Collectors.toList());

        return new ReporteConsolidadoResponse(
                year,
                mes,
                trabajadores
        );
    }

    private int getMonthNumber(String mes) {
        Map<String, Integer> meses = new HashMap<>();
        meses.put("Enero", 1);
        meses.put("Febrero", 2);
        meses.put("Marzo", 3);
        meses.put("Abril", 4);
        meses.put("Mayo", 5);
        meses.put("Junio", 6);
        meses.put("Julio", 7);
        meses.put("Agosto", 8);
        meses.put("Septiembre", 9);
        meses.put("Octubre", 10);
        meses.put("Noviembre", 11);
        meses.put("Diciembre", 12);
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

    // Clase auxiliar para acumular datos
    private static class TrabajadorData {

        String trabajadorId;
        String nombre;
        String ruc;
        String cargo;
        String cuenta;
        Map<Integer, String> horasPorDia;
        int totalHoras = 0;

        TrabajadorData(String id, String nombre, String ruc, String cargo, String cuenta, int daysInMonth) {
            this.trabajadorId = id;
            this.nombre = nombre;
            this.ruc = ruc;
            this.cargo = cargo;
            this.cuenta = cuenta;
            this.horasPorDia = new LinkedHashMap<>();
            for (int i = 1; i <= daysInMonth; i++) {
                horasPorDia.put(i, "");
            }
        }
    }
}
