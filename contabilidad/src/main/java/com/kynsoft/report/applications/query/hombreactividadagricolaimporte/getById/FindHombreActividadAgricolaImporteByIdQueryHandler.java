package com.kynsoft.report.applications.query.hombreactividadagricolaimporte.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.HombreActividadAgricolaImporteResponse;
import com.kynsoft.report.domain.dto.HombreActividadAgricolaImporteDto;
import com.kynsoft.report.domain.services.IHombreActividadAgricolaImporteService;
import org.springframework.stereotype.Component;

@Component
public class FindHombreActividadAgricolaImporteByIdQueryHandler 
    implements IQueryHandler<FindHombreActividadAgricolaImporteByIdQuery, HombreActividadAgricolaImporteResponse> {

    private final IHombreActividadAgricolaImporteService service;

    public FindHombreActividadAgricolaImporteByIdQueryHandler(IHombreActividadAgricolaImporteService service) {
        this.service = service;
    }

    @Override
    public HombreActividadAgricolaImporteResponse handle(FindHombreActividadAgricolaImporteByIdQuery query) {
        HombreActividadAgricolaImporteDto response = service.findById(query.getId());
        return new HombreActividadAgricolaImporteResponse(response);
    }
}