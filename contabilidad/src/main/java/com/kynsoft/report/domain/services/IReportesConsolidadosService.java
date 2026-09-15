package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.TipoSalida;
import com.kynsoft.report.domain.dto.reportes.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IReportesConsolidadosService {

    ReporteDeudasPendientesDto getDeudasPendientes(
            UUID fincaId,
            Double montoMinimo,
            Double montoMaximo,
            boolean incluirHistorial
    );

    ReporteFacturacionDto getFacturacion(
            UUID fincaId,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            TipoSalida tipo,
            String agruparPor
    );

    ReporteKardexDto getKardexConsolidado(
            UUID fincaId,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            List<UUID> productoIds
    );

    ReporteMovimientosGraficoDto getMovimientosGrafico(
            UUID fincaId,
            UUID productoId,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Granularidad granularidad
    );

    ResumenPagosDto getResumenPagos(
            UUID fincaId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    ResumenVentasDto getResumenVentas(
            UUID fincaId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );
}
