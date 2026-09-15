package com.kynsoft.report.applications.query.metricas.horasexcedidas;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.HorasExcedidasSummaryListResponse;
import com.kynsoft.report.domain.services.IReportesMetricasService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetHorasExcedidasSummaryQueryHandler implements IQueryHandler<GetHorasExcedidasSummaryQuery, HorasExcedidasSummaryListResponse> {

    private final IReportesMetricasService reportesMetricasService;

    @Override
    public HorasExcedidasSummaryListResponse handle(GetHorasExcedidasSummaryQuery query) {
        var items = reportesMetricasService.calcularHorasExcedidasSummary(query.getYear(), query.getMes());
        return new HorasExcedidasSummaryListResponse(items);
    }
}
