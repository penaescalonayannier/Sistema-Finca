package com.kynsoft.report.applications.query.almacen.porfinca;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IAlmacenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetAlmacenesPorFincaQueryHandler implements IQueryHandler<GetAlmacenesPorFincaQuery, PaginatedResponse> {

    private final IAlmacenService service;

    @Override
    public PaginatedResponse handle(GetAlmacenesPorFincaQuery query) {
        return service.findByFincaId(query.getFincaId(), query.getPageable());
    }
}
