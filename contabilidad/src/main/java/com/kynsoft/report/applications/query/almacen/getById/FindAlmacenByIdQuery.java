package com.kynsoft.report.applications.query.almacen.getById;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindAlmacenByIdQuery implements IQuery {
    private final UUID id;
}
