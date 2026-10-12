package com.kynsoft.report.applications.query.planingresoventa.getbyid;

import com.kynsoft.report.applications.query.responseObject.PlanIngresoVentaResponse;
import com.kynsoft.report.domain.services.IPlanIngresoVentaService;
import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindPlanIngresoVentaByIdQueryHandler
        implements IQueryHandler<FindPlanIngresoVentaByIdQuery, PlanIngresoVentaResponse> {
    private final IPlanIngresoVentaService service;
    @Override public PlanIngresoVentaResponse handle(FindPlanIngresoVentaByIdQuery query) {
        return new PlanIngresoVentaResponse(service.findById(query.getId()));
    }
}
