package com.kynsoft.report.applications.query.movimientostock.consolidado;

import com.kynsoft.report.domain.dto.reportes.ReporteMovimientosConsolidadoDto;
import com.kynsoft.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReporteMovimientosConsolidadoResponse implements IResponse {
    private final ReporteMovimientosConsolidadoDto data;
}
