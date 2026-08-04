package com.kynsoft.report.applications.query.reporte.consolidado;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetReporteConsolidadoQuery implements IQuery {
    private String year;
    private String mes;
}