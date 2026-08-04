package com.kynsoft.report.applications.query.reporte.trabajadoresExcedidos;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class GetTrabajadoresConHorasExcedidasQuery implements IQuery {
    private final String year;
    private final String mes;

    public GetTrabajadoresConHorasExcedidasQuery(String year, String mes) {
        this.year = year;
        this.mes = mes;
    }
}
