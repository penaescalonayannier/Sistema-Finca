package com.kynsoft.report.applications.query.campos.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindCamposByIdQuery implements IQuery {
    private UUID id;

    public FindCamposByIdQuery(UUID id) {
        this.id = id;
    }
}