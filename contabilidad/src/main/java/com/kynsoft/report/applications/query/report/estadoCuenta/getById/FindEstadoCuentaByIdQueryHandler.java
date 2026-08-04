package com.kynsoft.report.applications.query.report.estadoCuenta.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.EstadoCuentaResponse;
import com.kynsoft.report.domain.dto.EstadoCuentaDto;
import com.kynsoft.report.domain.services.IEstadoCuentaService;
import org.springframework.stereotype.Component;

@Component
public class FindEstadoCuentaByIdQueryHandler implements IQueryHandler<FindEstadoCuentaByIdQuery, EstadoCuentaResponse> {

    private final IEstadoCuentaService service;

    public FindEstadoCuentaByIdQueryHandler(IEstadoCuentaService service) {
        this.service = service;
    }

    @Override
    public EstadoCuentaResponse handle(FindEstadoCuentaByIdQuery query) {
        EstadoCuentaDto response = service.findById(query.getId());

        return new EstadoCuentaResponse(response);
    }
}
