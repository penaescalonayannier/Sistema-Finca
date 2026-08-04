package com.kynsoft.report.applications.query.report.cuenta110.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindCuenta110ByIdQuery  implements IQuery {

    private UUID id;

    public FindCuenta110ByIdQuery(UUID id) {
        this.id = id;
    }

}
