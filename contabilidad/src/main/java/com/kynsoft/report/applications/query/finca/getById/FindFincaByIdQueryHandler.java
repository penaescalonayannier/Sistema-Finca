package com.kynsoft.report.applications.query.finca.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.FincaResponse;
import com.kynsoft.report.domain.dto.FincaDto;
import com.kynsoft.report.domain.services.IFincaService;
import org.springframework.stereotype.Component;

@Component
public class FindFincaByIdQueryHandler 
    implements IQueryHandler<FindFincaByIdQuery, FincaResponse> {

    private final IFincaService service;

    public FindFincaByIdQueryHandler(IFincaService service) {
        this.service = service;
    }

    @Override
    public FincaResponse handle(FindFincaByIdQuery query) {
        FincaDto response = service.findById(query.getId());
        return new FincaResponse(response);
    }
}