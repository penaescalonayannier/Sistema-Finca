package com.kynsoft.report.applications.query.finca.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindFincaByIdQuery implements IQuery {
    private UUID id;

    public FindFincaByIdQuery(UUID id) {
        this.id = id;
    }
}