package com.kynsoft.report.applications.query.fincaproducto.getproductos;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetProductosDeFincaQuery implements IQuery {
    private final UUID fincaId;
}