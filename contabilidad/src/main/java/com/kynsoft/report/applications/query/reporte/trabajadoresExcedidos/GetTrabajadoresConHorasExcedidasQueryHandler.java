package com.kynsoft.report.applications.query.reporte.trabajadoresExcedidos;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.TrabajadorHorasExcedidasListResponse;
import com.kynsoft.report.applications.query.responseObject.TrabajadorHorasExcedidasResponse;
import com.kynsoft.report.infrastructure.entity.DiaTrabajo;
import com.kynsoft.report.infrastructure.entity.TrabajadorDia;
import com.kynsoft.report.infrastructure.repository.query.DiaTrabajoReadDataJPARepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
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

        // Mapa para agrupar trabajadores con horas excedidas: trabajadorId -> Datos
        Map<String, TrabajadorExcedidoData> trabajadoresExcedidos = new HashMap<>();

        // Procesar cada día
        for (DiaTrabajo dia : dias) {
            // Procesar cada trabajador del día
            for (TrabajadorDia td : dia.getTrabajadores()) {
                String horasStr = td.getHoras();

                if (horasStr != null && !horasStr.isEmpty()) {
                    double horas = convertirHorasANumero(horasStr);

                    // Si las horas exceden 8, agregar al registro
                    if (horas > 8.0) {
                        String trabajadorId = td.getTrabajador().getId().toString();

                        TrabajadorExcedidoData data = trabajadoresExcedidos.computeIfAbsent(
                                trabajadorId,
                                k -> new TrabajadorExcedidoData(
                                        trabajadorId,
                                        td.getTrabajador().getNombre(),
                                        td.getTrabajador().getRuc(),
                                        td.getTrabajador().getCargo() != null ? td.getTrabajador().getCargo().getName() : null
                                )
                        );

                        // Agregar el día excedido
                        data.diasExcedidos.add(new DiaExcedido(
                                formatearFecha(dia.getFecha()),
                                horas
                        ));
                    }
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
