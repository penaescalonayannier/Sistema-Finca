package com.kynsoft.report.applications.query.trabajadorReporte.getById;

import com.kynsoft.report.applications.query.reporte.getById.*;
import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindTrabajadorReporteByIdQuery implements IQuery {
    private UUID id;

    public FindTrabajadorReporteByIdQuery(UUID id) {
        this.id = id;
    }
}