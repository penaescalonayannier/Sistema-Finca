package com.kynsoft.report.applications.query.produccionterminada.getbyid;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindProduccionTerminadaByIdQuery implements IQuery {
    private final UUID id;
}
