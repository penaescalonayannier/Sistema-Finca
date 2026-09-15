package com.kynsoft.report.applications.query.movimientostock.consolidado;

import com.kynsoft.report.domain.dto.reportes.ReporteMovimientosConsolidadoDto;
import com.kynsoft.report.domain.services.IMovimientoStockService;
import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

@Component
public class GetReporteMovimientosConsolidadoQueryHandler implements IQueryHandler<GetReporteMovimientosConsolidadoQuery, ReporteMovimientosConsolidadoResponse> {

    private final IMovimientoStockService movimientoStockService;

    public GetReporteMovimientosConsolidadoQueryHandler(IMovimientoStockService movimientoStockService) {
        this.movimientoStockService = movimientoStockService;
    }

    @Override
    public ReporteMovimientosConsolidadoResponse handle(GetReporteMovimientosConsolidadoQuery query) {
        ReporteMovimientosConsolidadoDto consolidado = movimientoStockService.getConsolidadoMovimientos(
                query.getFechaInicio(),
                query.getFechaFin(),
                query.getFincaId()
        );
        return new ReporteMovimientosConsolidadoResponse(consolidado);
    }
}
