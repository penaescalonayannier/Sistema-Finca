package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.BalanceProductoDto;
import com.kynsoft.report.domain.dto.KardexDto;
import com.kynsoft.report.domain.dto.MovimientoStockDto;
import com.kynsoft.report.domain.dto.ResumenMovimientosDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.dto.reportes.ReporteMovimientosConsolidadoDto;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IMovimientoStockService {

    void registrar(MovimientoStockDto dto);

    void registrarMovimiento(UUID fincaProductoId, UUID fincaId, UUID productoId,
                             TipoMovimientoStock tipo, Integer cantidad,
                             Integer stockAnterior, Integer stockNuevo,
                             UUID referenciaId, String referenciaTabla, String descripcion);

    void registrarMovimiento(UUID fincaProductoId, UUID fincaId, UUID productoId,
                             TipoMovimientoStock tipo, Integer cantidad,
                             Integer stockAnterior, Integer stockNuevo,
                             UUID referenciaId, String referenciaTabla, String descripcion,
                             String centroCosto);

    void registrarMovimiento(UUID fincaProductoId, UUID fincaId, UUID productoId,
                             TipoMovimientoStock tipo, Integer cantidad,
                             Double stockAnterior, Double stockNuevo,
                             UUID referenciaId, String referenciaTabla, String descripcion);

    void registrarMovimiento(UUID fincaProductoId, UUID fincaId, UUID productoId,
                             TipoMovimientoStock tipo, Double cantidad,
                             Double stockAnterior, Double stockNuevo,
                             UUID referenciaId, String referenciaTabla, String descripcion);

    void registrarMovimiento(UUID fincaProductoId, UUID fincaId, UUID productoId,
                             TipoMovimientoStock tipo, Double cantidad,
                             Double stockAnterior, Double stockNuevo,
                             UUID referenciaId, String referenciaTabla, String descripcion,
                             String centroCosto);

    List<MovimientoStockDto> findByFincaProductoId(UUID fincaProductoId);

    List<MovimientoStockDto> findByFincaId(UUID fincaId);

    List<MovimientoStockDto> findByProductoId(UUID productoId);

    List<MovimientoStockDto> findByReferencia(UUID referenciaId, String referenciaTabla);

    List<MovimientoStockDto> findByFincaIdAndFechaBetween(UUID fincaId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    /**
     * Crea un ajuste manual de stock.
     * - Valida que tipo sea ENTRADA_AJUSTE o SALIDA_AJUSTE
     * - Valida cantidad > 0
     * - Valida observaciones no vacías (RN-04)
     * - Valida no stock negativo para SALIDA_AJUSTE (RN-06)
     */
    MovimientoStockDto crearAjuste(UUID almacenId, UUID fincaProductoId,
                                    TipoMovimientoStock tipo, Double cantidad,
                                    String observaciones);

    MovimientoStockDto findById(UUID id);

    List<MovimientoStockDto> findByAlmacenId(UUID almacenId);

    List<MovimientoStockDto> findByAlmacenIdAndFechaBetween(UUID almacenId,
                                                             LocalDateTime fechaInicio,
                                                             LocalDateTime fechaFin);

    /**
     * Obtiene movimientos filtrados por tipo
     */
    List<MovimientoStockDto> findByTipo(TipoMovimientoStock tipo);

    /**
     * Obtiene movimientos por tipo, finca y rango de fechas
     */
    List<MovimientoStockDto> findByTipoAndFincaIdAndFechaBetween(TipoMovimientoStock tipo,
                                                                   UUID fincaId,
                                                                   LocalDateTime fechaInicio,
                                                                   LocalDateTime fechaFin);

    /**
     * Genera resumen de entradas/salidas por período
     */
    ResumenMovimientosDto getResumen(UUID fincaId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Obtiene balance de movimientos por producto
     */
    List<BalanceProductoDto> getBalanceProducto(UUID fincaId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Genera Kardex (historial con saldo acumulado) para un producto
     */
    KardexDto getKardex(UUID fincaProductoId, UUID almacenId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Genera reporte consolidado de movimientos de stock.
     * Agrupa entradas por producto y salidas por destino en un rango de fechas.
     *
     * @param fechaInicio Fecha inicio del rango
     * @param fechaFin Fecha fin del rango
     * @param fincaId Opcional: filtrar por finca específica
     * @return Reporte consolidado con entradas por producto y salidas por destino
     */
    ReporteMovimientosConsolidadoDto getConsolidadoMovimientos(LocalDate fechaInicio, LocalDate fechaFin, UUID fincaId);
}
