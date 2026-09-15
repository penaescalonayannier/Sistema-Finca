package com.kynsoft.report.applications.query.metricas.ausentismo;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GetAbsentismoQuery implements IQuery {
    private String year;
    private String mes;
    private UUID trabajadorId;

    public GetAbsentismoQuery(String year, String mes) {
        this(year, mes, null);
    }
}
