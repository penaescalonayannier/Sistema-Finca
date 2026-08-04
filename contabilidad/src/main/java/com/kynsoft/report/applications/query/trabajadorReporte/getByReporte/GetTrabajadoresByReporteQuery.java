package com.kynsoft.report.applications.query.trabajadorReporte.getByReporte;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetTrabajadoresByReporteQuery implements IQuery {
    private UUID reporteId;
}