package com.kynsoft.report.applications.query.reporte.consolidadoPorResponsable;

import com.kynsoft.share.core.domain.bus.query.IQuery;

public class GetReporteConsolidadoPorResponsableQuery implements IQuery {
    private final String year;
    private final String mes;

    public GetReporteConsolidadoPorResponsableQuery(String year, String mes) {
        this.year = year;
        this.mes = mes;
    }

    public String getYear() {
        return year;
    }

    public String getMes() {
        return mes;
    }
}
