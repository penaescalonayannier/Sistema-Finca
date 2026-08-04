package com.kynsoft.report.applications.query.unidadmedida.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.UnidadMedidaResponse;
import com.kynsoft.report.domain.dto.UnidadMedidaDto;
import com.kynsoft.report.domain.services.IUnidadMedidaService;
import org.springframework.stereotype.Component;

@Component
public class FindUnidadMedidaByIdQueryHandler 
    implements IQueryHandler<FindUnidadMedidaByIdQuery, UnidadMedidaResponse> {

    private final IUnidadMedidaService service;

    public FindUnidadMedidaByIdQueryHandler(IUnidadMedidaService service) {
        this.service = service;
    }

    @Override
    public UnidadMedidaResponse handle(FindUnidadMedidaByIdQuery query) {
        UnidadMedidaDto response = service.findById(query.getId());
        return new UnidadMedidaResponse(response);
    }
}