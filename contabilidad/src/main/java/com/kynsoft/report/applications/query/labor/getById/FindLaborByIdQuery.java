package com.kynsoft.report.applications.query.labor.getById;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindLaborByIdQuery implements IQuery {
    private UUID id;

    public FindLaborByIdQuery(UUID id) {
        this.id = id;
    }
}