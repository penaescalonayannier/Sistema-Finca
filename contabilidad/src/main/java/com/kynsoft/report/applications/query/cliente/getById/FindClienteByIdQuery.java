package com.kynsoft.report.applications.query.cliente.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindClienteByIdQuery implements IQuery {

    private UUID id;

    public FindClienteByIdQuery(UUID id) {
        this.id = id;
    }
}
