package com.kynsoft.report.applications.query.salida.getbyid;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindSalidaByIdQuery implements IQuery {
    private UUID id;
}
