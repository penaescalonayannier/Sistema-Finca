package com.kynsoft.report.applications.query.metricas.rankings;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetRankingsQuery implements IQuery {
    private String year;
    private String mes;
    private String cargo;

    public GetRankingsQuery(String year, String mes) {
        this(year, mes, null);
    }
}
