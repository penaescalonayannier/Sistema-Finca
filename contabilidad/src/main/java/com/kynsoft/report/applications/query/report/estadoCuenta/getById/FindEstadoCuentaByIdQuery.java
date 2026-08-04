package com.kynsoft.report.applications.query.report.estadoCuenta.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindEstadoCuentaByIdQuery  implements IQuery {

    private UUID id;

    public FindEstadoCuentaByIdQuery(UUID id) {
        this.id = id;
    }

}
