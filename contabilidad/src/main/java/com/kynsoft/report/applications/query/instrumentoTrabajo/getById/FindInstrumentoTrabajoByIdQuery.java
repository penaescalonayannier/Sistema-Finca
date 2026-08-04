package com.kynsoft.report.applications.query.instrumentoTrabajo.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindInstrumentoTrabajoByIdQuery implements IQuery {
    private UUID id;

    public FindInstrumentoTrabajoByIdQuery(UUID id) {
        this.id = id;
    }
}