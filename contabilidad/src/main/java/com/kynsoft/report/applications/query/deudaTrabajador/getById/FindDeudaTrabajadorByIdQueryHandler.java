package com.kynsoft.report.applications.query.deudaTrabajador.getById;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.DeudaTrabajadorResponse;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDto;
import com.kynsoft.report.domain.services.IDeudaTrabajadorService;
import org.springframework.stereotype.Component;

@Component
public class FindDeudaTrabajadorByIdQueryHandler implements IQueryHandler<FindDeudaTrabajadorByIdQuery, DeudaTrabajadorResponse> {

    private final IDeudaTrabajadorService service;

    public FindDeudaTrabajadorByIdQueryHandler(IDeudaTrabajadorService service) {
        this.service = service;
    }

    @Override
    public DeudaTrabajadorResponse handle(FindDeudaTrabajadorByIdQuery query) {
        DeudaTrabajadorDto dto = service.findById(query.getId());
        return new DeudaTrabajadorResponse(dto);
    }
}
