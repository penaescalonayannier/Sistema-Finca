package com.kynsoft.report.applications.query.reporte.trabajadoresExcedidos;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.TrabajadorHorasExcedidasListResponse;
import com.kynsoft.report.applications.query.responseObject.TrabajadorHorasExcedidasResponse;
import com.kynsoft.report.infrastructure.entity.DiaTrabajo;
import com.kynsoft.report.infrastructure.entity.TrabajadorDia;
import com.kynsoft.report.infrastructure.repository.query.DiaTrabajoReadDataJPARepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GetTrabajadoresConHorasExcedidasQueryHandler
        implements IQueryHandler<GetTrabajadoresConHorasExcedidasQuery, TrabajadorHorasExcedidasListResponse> {

    private final DiaTrabajoReadDataJPARepository diaTrabajoRepository;

    @Override
    public TrabajadorHorasExcedidasListResponse handle(GetTrabajadoresConHorasExcedidasQuery query) {
        String year = query.getYear();
        String mes = query.getMes();

        // Obtener todos los días del mes con sus trabajadores
        List<DiaTrabajo> dias = diaTrabajoRepository.findByYearAndMesWithTrabajadores(year, mes);

        if (dias.isEmpty()) {
            return new TrabajadorHorasExcedidasListResponse(Collections.emptyList());
        }

        // Paso 1: Consolidar horas por trabajador y fecha
        // Estructura: trabajadorId -> { fecha -> horasTotales }
        Map<String, Map<LocalDate, Double>> horasPorTrabajadorYFecha = new HashMap<>();
        // Guardar datos del trabajador: trabajadorId -> TrabajadorInfo
        Map<String, TrabajadorInfo> trabajadoresInfo = new HashMap<>();

        for (DiaTrabajo dia : dias) {
            LocalDate fecha = dia.getFecha();

            for (TrabajadorDia td : dia.getTrabajadores()) {
                String horasStr = td.getHoras();

                if (horasStr != null && !horasStr.isEmpty()) {
                    double horas = convertirHorasANumero(horasStr);
                    String trabajadorId = td.getTrabajador().getId().toString();

                    // Guardar info del trabajador si no existe
                    trabajadoresInfo.computeIfAbsent(trabajadorId, k -> new TrabajadorInfo(
                            trabajadorId,
                            td.getTrabajador().getNombre(),
                            td.getTrabajador().getRuc(),
                            td.getTrabajador().getCargo() != null ? td.getTrabajador().getCargo().getName() : null
                    ));

                    // Sumar horas para este trabajador en esta fecha
                    horasPorTrabajadorYFecha
                            .computeIfAbsent(trabajadorId, k -> new HashMap<>())
                            .merge(fecha, horas, Double::sum);
                }
            }
        }

        // Paso 2: Identificar trabajadores con días excedidos (horas totales > 8)
        Map<String, TrabajadorExcedidoData> trabajadoresExcedidos = new HashMap<>();

        for (Map.Entry<String, Map<LocalDate, Double>> entry : horasPorTrabajadorYFecha.entrySet()) {
            String trabajadorId = entry.getKey();
            Map<LocalDate, Double> horasPorFecha = entry.getValue();

            for (Map.Entry<LocalDate, Double> fechaEntry : horasPorFecha.entrySet()) {
                LocalDate fecha = fechaEntry.getKey();
                double horasTotales = fechaEntry.getValue();

                if (horasTotales > 8.0) {
                    TrabajadorInfo info = trabajadoresInfo.get(trabajadorId);

                    TrabajadorExcedidoData data = trabajadoresExcedidos.computeIfAbsent(
                            trabajadorId,
                            k -> new TrabajadorExcedidoData(info.trabajadorId, info.nombre, info.ruc, info.cargo)
                    );

                    data.diasExcedidos.add(new DiaExcedido(formatearFecha(fecha), horasTotales));
                }
            }
        }

        // Convertir a respuesta y ordenar por nombre
        List<TrabajadorHorasExcedidasResponse> items = trabajadoresExcedidos.values().stream()
                .map(data -> new TrabajadorHorasExcedidasResponse(
                        data.trabajadorId,
                        data.nombre,
                        data.ruc,
                        data.cargo,
                        data.diasExcedidos.stream()
                                .sorted(Comparator.comparing(d -> d.fecha))
                                .map(d -> new TrabajadorHorasExcedidasResponse.DiaExcedidoResponse(d.fecha, d.horas))
                                .collect(Collectors.toList())
                ))
                .sorted(Comparator.comparing(TrabajadorHorasExcedidasResponse::getNombre))
                .collect(Collectors.toList());

        return new TrabajadorHorasExcedidasListResponse(items);
    }

    private double convertirHorasANumero(String horas) {
        try {
            return Double.parseDouble(horas);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String formatearFecha(LocalDate fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha.format(formatter);
    }

    // Clase interna para almacenar info básica del trabajador
    private static class TrabajadorInfo {
        String trabajadorId;
        String nombre;
        String ruc;
        String cargo;

        TrabajadorInfo(String trabajadorId, String nombre, String ruc, String cargo) {
            this.trabajadorId = trabajadorId;
            this.nombre = nombre;
            this.ruc = ruc;
            this.cargo = cargo;
        }
    }

    // Clase interna para almacenar datos temporales
    private static class TrabajadorExcedidoData {
        String trabajadorId;
        String nombre;
        String ruc;
        String cargo;
        List<DiaExcedido> diasExcedidos = new ArrayList<>();

        TrabajadorExcedidoData(String trabajadorId, String nombre, String ruc, String cargo) {
            this.trabajadorId = trabajadorId;
            this.nombre = nombre;
            this.ruc = ruc;
            this.cargo = cargo;
        }
    }

    private static class DiaExcedido {
        String fecha;
        double horas;

        DiaExcedido(String fecha, double horas) {
            this.fecha = fecha;
            this.horas = horas;
        }
    }
}
