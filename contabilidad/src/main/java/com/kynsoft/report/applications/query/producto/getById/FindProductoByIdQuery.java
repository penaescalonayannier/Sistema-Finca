package com.kynsoft.report.applications.query.producto.getById;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindProductoByIdQuery implements IQuery {
    private UUID id;

    public FindProductoByIdQuery(UUID id) {
        this.id = id;
    }
}