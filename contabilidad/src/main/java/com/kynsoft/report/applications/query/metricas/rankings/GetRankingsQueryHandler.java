package com.kynsoft.report.applications.query.metricas.rankings;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.RankingsListResponse;
import com.kynsoft.report.domain.services.IReportesMetricasService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetRankingsQueryHandler implements IQueryHandler<GetRankingsQuery, RankingsListResponse> {

    private final IReportesMetricasService reportesMetricasService;

    @Override
    public RankingsListResponse handle(GetRankingsQuery query) {
        var items = reportesMetricasService.calcularRankings(query.getYear(), query.getMes(), query.getCargo());
        return new RankingsListResponse(items);
    }
}
