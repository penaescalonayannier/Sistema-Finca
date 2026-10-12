package com.kynsoft.report.applications.query.planingresoventa.search;

import com.kynsoft.report.domain.services.IPlanIngresoVentaService;
import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetSearchPlanIngresoVentaQueryHandler
        implements IQueryHandler<GetSearchPlanIngresoVentaQuery, PaginatedResponse> {
    private final IPlanIngresoVentaService service;
    @Override public PaginatedResponse handle(GetSearchPlanIngresoVentaQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
