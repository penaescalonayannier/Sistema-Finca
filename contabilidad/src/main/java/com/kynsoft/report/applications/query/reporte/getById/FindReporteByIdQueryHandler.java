package com.kynsoft.report.applications.query.reporte.getById;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.ReporteResponse;
import com.kynsoft.report.domain.dto.ReporteDto;
import com.kynsoft.report.domain.services.IReporteService;
import org.springframework.stereotype.Component;

@Component
public class FindReporteByIdQueryHandler
        implements IQueryHandler<FindReporteByIdQuery, ReporteResponse> {

    private final IReporteService reportService;

    public FindReporteByIdQueryHandler(IReporteService reportService) {
        this.reportService = reportService;
    }

    @Override
    public ReporteResponse handle(FindReporteByIdQuery query) {
        ReporteDto response = reportService.findById(query.getId());
        return new ReporteResponse(response);
    }
}
