package com.kynsoft.report.applications.query.cepa.getById;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindCepaByIdQuery implements IQuery {
    private UUID id;

    public FindCepaByIdQuery(UUID id) {
        this.id = id;
    }
}