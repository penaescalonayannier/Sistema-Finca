package com.kynsoft.report.applications.query.trabajador.porFinca;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetTrabajadoresPorFincaQuery implements IQuery {
    private UUID fincaId;
    private int page;
    private int pageSize;
}
