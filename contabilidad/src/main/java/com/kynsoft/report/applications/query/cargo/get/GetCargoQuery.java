package com.kynsoft.report.applications.query.cargo.get;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GetCargoQuery implements IQuery {
    private UUID id;

    public GetCargoQuery(UUID id) {
        this.id = id;
    }
}
