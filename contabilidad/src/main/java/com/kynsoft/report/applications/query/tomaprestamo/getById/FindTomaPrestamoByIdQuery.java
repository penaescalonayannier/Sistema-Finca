package com.kynsoft.report.applications.query.tomaprestamo.getById;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindTomaPrestamoByIdQuery implements IQuery {
    private UUID id;

    public FindTomaPrestamoByIdQuery(UUID id) {
        this.id = id;
    }
}