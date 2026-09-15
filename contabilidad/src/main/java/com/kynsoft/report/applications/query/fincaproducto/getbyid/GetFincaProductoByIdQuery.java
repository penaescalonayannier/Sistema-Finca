package com.kynsoft.report.applications.query.fincaproducto.getbyid;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetFincaProductoByIdQuery implements IQuery {
    private UUID id;
}
