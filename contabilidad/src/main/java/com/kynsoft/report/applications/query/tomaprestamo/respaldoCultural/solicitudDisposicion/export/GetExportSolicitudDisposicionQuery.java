package com.kynsoft.report.applications.query.tomaprestamo.respaldoCultural.solicitudDisposicion.export;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GetExportSolicitudDisposicionQuery implements IQuery {
    private final UUID tomaPrestamoId;
}