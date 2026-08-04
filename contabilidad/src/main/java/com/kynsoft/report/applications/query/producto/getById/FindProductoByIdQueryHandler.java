package com.kynsoft.report.applications.query.producto.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.ProductoResponse;
import com.kynsoft.report.domain.dto.ProductoDto;
import com.kynsoft.report.domain.services.IProductoService;
import org.springframework.stereotype.Component;

@Component
public class FindProductoByIdQueryHandler 
    implements IQueryHandler<FindProductoByIdQuery, ProductoResponse> {

    private final IProductoService service;

    public FindProductoByIdQueryHandler(IProductoService service) {
        this.service = service;
    }

    @Override
    public ProductoResponse handle(FindProductoByIdQuery query) {
        ProductoDto response = service.findById(query.getId());
        return new ProductoResponse(response);
    }
}