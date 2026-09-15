package com.kynsoft.report.applications.query.movimientostock.consolidado;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class GetReporteMovimientosConsolidadoQuery implements IQuery {

    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final UUID fincaId; // Optional: filter by finca

    public GetReporteMovimientosConsolidadoQuery(LocalDate fechaInicio, LocalDate fechaFin, UUID fincaId) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fincaId = fincaId;
    }
}
