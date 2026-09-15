package com.kynsoft.report.applications.query.tomaprestamo.getById;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.TomaPrestamoResponse;
import com.kynsoft.report.domain.dto.TomaPrestamoDto;
import com.kynsoft.report.domain.services.ITomaPrestamoService;
import org.springframework.stereotype.Component;

@Component
public class FindTomaPrestamoByIdQueryHandler 
    implements IQueryHandler<FindTomaPrestamoByIdQuery, TomaPrestamoResponse> {

    private final ITomaPrestamoService service;

    public FindTomaPrestamoByIdQueryHandler(ITomaPrestamoService service) {
        this.service = service;
    }

    @Override
    public TomaPrestamoResponse handle(FindTomaPrestamoByIdQuery query) {
        TomaPrestamoDto response = service.findById(query.getId());
        return new TomaPrestamoResponse(response);
    }
}