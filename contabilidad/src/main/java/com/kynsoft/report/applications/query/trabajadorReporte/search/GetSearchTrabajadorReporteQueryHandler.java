package com.kynsoft.report.applications.query.trabajadorReporte.search;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.ITrabajadorReporteService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchTrabajadorReporteQueryHandler 
    implements IQueryHandler<GetSearchTrabajadorReporteQuery, PaginatedResponse> {

    private final ITrabajadorReporteService reportService;

    public GetSearchTrabajadorReporteQueryHandler(ITrabajadorReporteService reportService) {
        this.reportService = reportService;
    }

    @Override
    public PaginatedResponse handle(GetSearchTrabajadorReporteQuery query) {
        return reportService.search(query.getPageable(), query.getFilter());
    }
}