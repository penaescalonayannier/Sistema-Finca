package com.kynsoft.report.applications.query.diatrabajo.getByReporte;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetDiasByReporteQuery implements IQuery {
    private UUID reporteId;
}