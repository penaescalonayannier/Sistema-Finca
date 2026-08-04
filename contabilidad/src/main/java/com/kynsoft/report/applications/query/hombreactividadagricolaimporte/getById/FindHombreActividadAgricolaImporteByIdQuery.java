package com.kynsoft.report.applications.query.hombreactividadagricolaimporte.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindHombreActividadAgricolaImporteByIdQuery implements IQuery {
    private UUID id;

    public FindHombreActividadAgricolaImporteByIdQuery(UUID id) {
        this.id = id;
    }
}