package com.kynsoft.report.applications.query.labor.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.LaborResponse;
import com.kynsoft.report.domain.dto.LaborDto;
import com.kynsoft.report.domain.services.ILaborService;
import org.springframework.stereotype.Component;

@Component
public class FindLaborByIdQueryHandler 
    implements IQueryHandler<FindLaborByIdQuery, LaborResponse> {

    private final ILaborService service;

    public FindLaborByIdQueryHandler(ILaborService service) {
        this.service = service;
    }

    @Override
    public LaborResponse handle(FindLaborByIdQuery query) {
        LaborDto response = service.findById(query.getId());
        return new LaborResponse(response);
    }
}