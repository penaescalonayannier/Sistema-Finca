package com.kynsoft.report.applications.query.cliente.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.ClienteResponse;
import com.kynsoft.report.domain.dto.ClienteDto;
import com.kynsoft.report.domain.services.IClienteService;
import org.springframework.stereotype.Component;

@Component
public class FindClienteByIdQueryHandler implements IQueryHandler<FindClienteByIdQuery, ClienteResponse> {

    private final IClienteService service;

    public FindClienteByIdQueryHandler(IClienteService service) {
        this.service = service;
    }

    @Override
    public ClienteResponse handle(FindClienteByIdQuery query) {
        ClienteDto response = service.findById(query.getId());

        return new ClienteResponse(response);
    }
}
