package com.kynsoft.report.applications.query.fincaproducto.alertas;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IFincaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetAlertasStockBajoQueryHandler implements IQueryHandler<GetAlertasStockBajoQuery, PaginatedResponse> {

    private final IFincaProductoService service;

    @Override
    public PaginatedResponse handle(GetAlertasStockBajoQuery query) {
        return service.getAlertasStockBajo(query.getPageable());
    }
}
