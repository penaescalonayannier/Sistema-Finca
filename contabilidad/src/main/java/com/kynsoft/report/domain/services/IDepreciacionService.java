package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.MovimientoDepreciacionDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para cálculo y gestión de Depreciación de Activos Fijos.
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP): Método de depreciación línea recta
 * - Resolución 51/2021 MFP: Tasas máximas de depreciación
 *
 * Fórmula según NCC No. 7:
 * Depreciación Anual = (Valor Adquisición - Valor Residual Estimado) × Tasa%
 * Depreciación Mensual = Depreciación Anual / 12
 */
public interface IDepreciacionService {

    /**
     * Calcula la depreciación mensual para un activo específico.
     */
    BigDecimal calcularDepreciacionMensual(UUID activoFijoId);

    /**
     * Ejecuta el cierre de depreciación mensual para todos los activos.
     * Crea los movimientos de depreciación correspondientes.
     *
     * @param mes Mes del cierre (1-12)
     * @param anio Año del cierre
     * @return Lista de movimientos creados
     */
    List<MovimientoDepreciacionDto> ejecutarCierreMensual(int mes, int anio);

    /**
     * Ejecuta el cierre de depreciación para un activo específico.
     */
    MovimientoDepreciacionDto ejecutarCierreActivo(UUID activoFijoId, int mes, int anio);

    /**
     * Obtiene los movimientos de depreciación de un activo.
     */
    List<MovimientoDepreciacionDto> findByActivoFijo(UUID activoFijoId);

    /**
     * Obtiene los movimientos de depreciación de un período.
     */
    List<MovimientoDepreciacionDto> findByPeriodo(int mes, int anio);

    /**
     * Genera reporte de depreciación anual.
     */
    List<ReporteDepreciacionDto> generarReporteAnual(int anio);

    /**
     * Verifica si ya existe cierre para un período.
     */
    boolean existeCierre(int mes, int anio);

    /**
     * DTO para reporte de depreciación.
     */
    record ReporteDepreciacionDto(
            UUID activoFijoId,
            String numeroInventario,
            String descripcion,
            String grupoCodigo,
            BigDecimal valorAdquisicion,
            BigDecimal depreciacionAnual,
            BigDecimal depreciacionAcumulada,
            BigDecimal valorResidual,
            BigDecimal tasaAplicada
    ) {}
}
