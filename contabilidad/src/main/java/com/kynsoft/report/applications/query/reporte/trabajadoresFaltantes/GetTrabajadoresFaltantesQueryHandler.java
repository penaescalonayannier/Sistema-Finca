package com.kynsoft.report.applications.query.reporte.trabajadoresFaltantes;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.TrabajadoresFaltantesListResponse;
import com.kynsoft.report.applications.query.responseObject.TrabajadoresFaltantesResponse;
import com.kynsoft.report.infrastructure.entity.DiaTrabajo;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.entity.TrabajadorDia;
import com.kynsoft.report.infrastructure.repository.query.DiaTrabajoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GetTrabajadoresFaltantesQueryHandler
        implements IQueryHandler<GetTrabajadoresFaltantesQuery, TrabajadoresFaltantesListResponse> {

    private final TrabajadorReadDataJPARepository trabajadorRepository;
    private final DiaTrabajoReadDataJPARepository diaTrabajoRepository;

    @Override
    public TrabajadoresFaltantesListResponse handle(GetTrabajadoresFaltantesQuery query) {
        String year = query.getYear();
        String mes = query.getMes();

        // Obtener todos los trabajadores activos
        List<Trabajador> todosTrabajadores = trabajadorRepository.findAll((root, q, cb) -> cb.equal(root.get("activo"), true));

        if (todosTrabajadores.isEmpty()) {
            return new TrabajadoresFaltantesListResponse(Collections.emptyList());
        }

        // Obtener todos los días del mes con sus trabajadores
        List<DiaTrabajo> dias = diaTrabajoRepository.findByYearAndMesWithTrabajadores(year, mes);

        // Crear un Set de IDs de trabajadores que tienen reportes en el consolidado
        Set<String> trabajadoresReportados = new HashSet<>();
        for (DiaTrabajo dia : dias) {
            for (TrabajadorDia td : dia.getTrabajadores()) {
                trabajadoresReportados.add(td.getTrabajador().getId().toString());
            }
        }

        // Encontrar trabajadores NO reportados (faltantes)
        List<TrabajadoresFaltantesResponse> trabajadoresFaltantes = todosTrabajadores.stream()
                .filter(t -> !trabajadoresReportados.contains(t.getId().toString()))
                .map(t -> new TrabajadoresFaltantesResponse(
                        t.getId().toString(),
                        t.getNombre(),
                        t.getRuc(),
                        t.getCargo() != null ? t.getCargo().getName() : null,
                        t.getCuenta()
                ))
                .sorted(Comparator.comparing(TrabajadoresFaltantesResponse::getNombre))
                .collect(Collectors.toList());

        return new TrabajadoresFaltantesListResponse(trabajadoresFaltantes);
    }
}
