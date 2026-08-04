package com.kynsoft.report.applications.query.bloque.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.BloqueResponse;
import com.kynsoft.report.domain.dto.BloqueDto;
import com.kynsoft.report.domain.services.IBloqueService;
import org.springframework.stereotype.Component;

@Component
public class FindBloqueByIdQueryHandler 
    implements IQueryHandler<FindBloqueByIdQuery, BloqueResponse> {

    private final IBloqueService service;

    public FindBloqueByIdQueryHandler(IBloqueService service) {
        this.service = service;
    }

    @Override
    public BloqueResponse handle(FindBloqueByIdQuery query) {
        BloqueDto response = service.findById(query.getId());
        return new BloqueResponse(response);
    }
}