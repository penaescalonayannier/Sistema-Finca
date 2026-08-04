package com.kynsoft.report.applications.query.unidadmedida.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindUnidadMedidaByIdQuery implements IQuery {
    private UUID id;

    public FindUnidadMedidaByIdQuery(UUID id) {
        this.id = id;
    }
}