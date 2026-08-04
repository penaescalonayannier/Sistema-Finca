package com.kynsoft.report.applications.query.reporte.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindReporteByIdQuery implements IQuery {
    private UUID id;

    public FindReporteByIdQuery(UUID id) {
        this.id = id;
    }
}