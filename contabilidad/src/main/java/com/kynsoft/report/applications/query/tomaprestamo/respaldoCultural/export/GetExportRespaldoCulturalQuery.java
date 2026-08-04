package com.kynsoft.report.applications.query.tomaprestamo.respaldoCultural.export;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GetExportRespaldoCulturalQuery implements IQuery {
    private final UUID tomaPrestamoId;
}