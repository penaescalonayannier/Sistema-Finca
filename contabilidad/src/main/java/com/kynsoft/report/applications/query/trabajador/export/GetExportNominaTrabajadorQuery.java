package com.kynsoft.report.applications.query.trabajador.export;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetExportNominaTrabajadorQuery implements IQuery {
    private final List<UUID> trabajadoresIds;
}