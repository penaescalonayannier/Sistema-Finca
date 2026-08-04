package com.kynsoft.report.applications.query;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GetEvaluacionQuery implements IQuery {
    private UUID id;

    public GetEvaluacionQuery(UUID id) {
        this.id = id;
    }
}
