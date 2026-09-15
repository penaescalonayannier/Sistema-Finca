package com.kynsoft.report.applications.query.metricas.ausentismo;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.AbsentismoListResponse;
import com.kynsoft.report.domain.services.IReportesMetricasService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetAbsentismoQueryHandler implements IQueryHandler<GetAbsentismoQuery, AbsentismoListResponse> {

    private final IReportesMetricasService reportesMetricasService;

    @Override
    public AbsentismoListResponse handle(GetAbsentismoQuery query) {
        var items = reportesMetricasService.calcularAbsentismo(query.getYear(), query.getMes(), query.getTrabajadorId());
        return new AbsentismoListResponse(items);
    }
}
