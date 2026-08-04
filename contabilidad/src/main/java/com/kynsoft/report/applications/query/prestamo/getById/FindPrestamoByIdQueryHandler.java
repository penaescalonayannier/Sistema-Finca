package com.kynsoft.report.applications.query.prestamo.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.PrestamoResponse;
import com.kynsoft.report.domain.dto.PrestamoDto;
import com.kynsoft.report.domain.services.IPrestamoService;
import org.springframework.stereotype.Component;

@Component
public class FindPrestamoByIdQueryHandler implements IQueryHandler<FindPrestamoByIdQuery, PrestamoResponse> {

    private final IPrestamoService service;

    public FindPrestamoByIdQueryHandler(IPrestamoService service) {
        this.service = service;
    }

    @Override
    public PrestamoResponse handle(FindPrestamoByIdQuery query) {
        PrestamoDto response = service.findById(query.getId());

        return new PrestamoResponse(response);
    }
}
