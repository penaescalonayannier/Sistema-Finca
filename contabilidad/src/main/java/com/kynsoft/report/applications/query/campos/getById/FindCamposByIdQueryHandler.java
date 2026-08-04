package com.kynsoft.report.applications.query.campos.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.CamposResponse;
import com.kynsoft.report.domain.dto.CampoDto;
import com.kynsoft.report.domain.services.ICamposService;
import org.springframework.stereotype.Component;

@Component
public class FindCamposByIdQueryHandler 
    implements IQueryHandler<FindCamposByIdQuery, CamposResponse> {

    private final ICamposService service;

    public FindCamposByIdQueryHandler(ICamposService service) {
        this.service = service;
    }

    @Override
    public CamposResponse handle(FindCamposByIdQuery query) {
        CampoDto response = service.findById(query.getId());
        return new CamposResponse(response);
    }
}