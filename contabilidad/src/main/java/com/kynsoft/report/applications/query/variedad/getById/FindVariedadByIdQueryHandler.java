package com.kynsoft.report.applications.query.variedad.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.VariedadResponse;
import com.kynsoft.report.domain.dto.VariedadDto;
import com.kynsoft.report.domain.services.IVariedadService;
import org.springframework.stereotype.Component;

@Component
public class FindVariedadByIdQueryHandler 
    implements IQueryHandler<FindVariedadByIdQuery, VariedadResponse> {

    private final IVariedadService service;

    public FindVariedadByIdQueryHandler(IVariedadService service) {
        this.service = service;
    }

    @Override
    public VariedadResponse handle(FindVariedadByIdQuery query) {
        VariedadDto response = service.findById(query.getId());
        return new VariedadResponse(response);
    }
}