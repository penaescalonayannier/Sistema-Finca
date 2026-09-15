package com.kynsoft.report.applications.query.reporte.search;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IReporteService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchReporteQueryHandler 
    implements IQueryHandler<GetSearchReporteQuery, PaginatedResponse> {

    private final IReporteService reportService;

    public GetSearchReporteQueryHandler(IReporteService reportService) {
        this.reportService = reportService;
    }

    @Override
    public PaginatedResponse handle(GetSearchReporteQuery query) {
        return reportService.search(query.getPageable(), query.getFilter());
    }
}