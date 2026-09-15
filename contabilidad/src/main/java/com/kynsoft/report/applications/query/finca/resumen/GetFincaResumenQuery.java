package com.kynsoft.report.applications.query.finca.resumen;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetFincaResumenQuery implements IQuery {
    private UUID id;
}
