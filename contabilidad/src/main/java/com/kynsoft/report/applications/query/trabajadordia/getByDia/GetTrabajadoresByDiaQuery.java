package com.kynsoft.report.applications.query.trabajadordia.getByDia;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetTrabajadoresByDiaQuery implements IQuery {
    private UUID diaTrabajoId;
}