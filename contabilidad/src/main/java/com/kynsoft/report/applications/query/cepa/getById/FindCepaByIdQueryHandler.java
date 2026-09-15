package com.kynsoft.report.applications.query.cepa.getById;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.CepaResponse;
import com.kynsoft.report.domain.dto.CepaDto;
import com.kynsoft.report.domain.services.ICepaService;
import org.springframework.stereotype.Component;

@Component
public class FindCepaByIdQueryHandler 
    implements IQueryHandler<FindCepaByIdQuery, CepaResponse> {

    private final ICepaService service;

    public FindCepaByIdQueryHandler(ICepaService service) {
        this.service = service;
    }

    @Override
    public CepaResponse handle(FindCepaByIdQuery query) {
        CepaDto response = service.findById(query.getId());
        return new CepaResponse(response);
    }
}