package com.kynsoft.report.applications.query.bloque.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindBloqueByIdQuery implements IQuery {
    private UUID id;

    public FindBloqueByIdQuery(UUID id) {
        this.id = id;
    }
}