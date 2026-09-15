package com.kynsoft.report.applications.query.metricas.horasexcedidas;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetHorasExcedidasSummaryQuery implements IQuery {
    private String year;
    private String mes;
}
