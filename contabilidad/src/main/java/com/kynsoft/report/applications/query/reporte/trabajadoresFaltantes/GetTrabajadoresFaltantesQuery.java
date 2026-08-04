package com.kynsoft.report.applications.query.reporte.trabajadoresFaltantes;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class GetTrabajadoresFaltantesQuery implements IQuery {
    private final String year;
    private final String mes;

    public GetTrabajadoresFaltantesQuery(String year, String mes) {
        this.year = year;
        this.mes = mes;
    }
}
