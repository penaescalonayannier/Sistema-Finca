package com.kynsoft.report.applications.query.grupo.get;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GetGrupoQuery implements IQuery {
    private UUID id;

    public GetGrupoQuery(UUID id) {
        this.id = id;
    }
}
