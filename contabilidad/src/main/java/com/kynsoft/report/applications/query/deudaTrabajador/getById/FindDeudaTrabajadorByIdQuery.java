package com.kynsoft.report.applications.query.deudaTrabajador.getById;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindDeudaTrabajadorByIdQuery implements IQuery {

    private final UUID id;

    public FindDeudaTrabajadorByIdQuery(UUID id) {
        this.id = id;
    }
}
