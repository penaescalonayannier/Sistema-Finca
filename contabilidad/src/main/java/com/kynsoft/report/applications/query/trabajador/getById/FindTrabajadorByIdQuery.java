package com.kynsoft.report.applications.query.trabajador.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindTrabajadorByIdQuery implements IQuery {
    private UUID id;

    public FindTrabajadorByIdQuery(UUID id) {
        this.id = id;
    }
}