package com.kynsoft.report.applications.query.prestamo.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindPrestamoByIdQuery implements IQuery {

    private UUID id;

    public FindPrestamoByIdQuery(UUID id) {
        this.id = id;
    }
}
