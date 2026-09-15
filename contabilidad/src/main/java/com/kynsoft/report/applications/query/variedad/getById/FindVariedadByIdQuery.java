package com.kynsoft.report.applications.query.variedad.getById;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindVariedadByIdQuery implements IQuery {
    private UUID id;

    public FindVariedadByIdQuery(UUID id) {
        this.id = id;
    }
}