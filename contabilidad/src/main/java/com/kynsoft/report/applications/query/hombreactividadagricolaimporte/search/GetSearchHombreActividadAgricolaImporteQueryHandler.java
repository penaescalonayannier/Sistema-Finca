package com.kynsoft.report.applications.query.hombreactividadagricolaimporte.search;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IHombreActividadAgricolaImporteService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchHombreActividadAgricolaImporteQueryHandler 
    implements IQueryHandler<GetSearchHombreActividadAgricolaImporteQuery, PaginatedResponse> {

    private final IHombreActividadAgricolaImporteService service;

    public GetSearchHombreActividadAgricolaImporteQueryHandler(IHombreActividadAgricolaImporteService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchHombreActividadAgricolaImporteQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}