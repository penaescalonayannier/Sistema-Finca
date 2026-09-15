package com.kynsoft.report.applications.query.trabajadorReporte.getByReporte;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.TrabajadorReporteDetailListResponse;
import com.kynsoft.report.applications.query.responseObject.TrabajadorReporteDetailResponse;
import com.kynsoft.report.infrastructure.entity.TrabajadorReporte;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReporteReadDataJPARepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GetTrabajadoresByReporteQueryHandler
        implements IQueryHandler<GetTrabajadoresByReporteQuery, TrabajadorReporteDetailListResponse> {

    private final TrabajadorReporteReadDataJPARepository repositoryQuery;

    @Override
    public TrabajadorReporteDetailListResponse handle(GetTrabajadoresByReporteQuery query) {
        List<TrabajadorReporte> asignaciones = repositoryQuery.findByReporteId(query.getReporteId());

        List<TrabajadorReporteDetailResponse> responses = asignaciones.stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());

        return new TrabajadorReporteDetailListResponse(
                responses,
                (long) responses.size(),
                1,
                0,
                responses.size()
        );
    }

    private TrabajadorReporteDetailResponse toDetailResponse(TrabajadorReporte tr) {
        return new TrabajadorReporteDetailResponse(
                tr.getId(),
                tr.getTrabajador().getId(),
                tr.getTrabajador().getNombre(),
                tr.getTrabajador().getRuc(),
                tr.getTrabajador().getCuenta(),
                tr.getTrabajador().getCargo() != null ? tr.getTrabajador().getCargo().getName() : null,
                tr.getReporte().getId(),
                tr.getReporte().getCodigo(),
                tr.getReporte().getBloque(),
                tr.getReporte().getCampo(),
                tr.getReporte().getArea(),
                tr.getReporte().getNorma(),
                "",
                tr.getReporte().getYear(),
                tr.getReporte().getMes(),
                tr.getNorma(),
                tr.getHoras()
        );
    }
}
