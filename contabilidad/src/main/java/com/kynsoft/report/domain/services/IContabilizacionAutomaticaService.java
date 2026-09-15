package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.AsientoContableDto;
import com.kynsoft.report.domain.dto.MovimientoStockDto;
import com.kynsoft.report.domain.dto.ReglaContabilizacionDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for automatic accounting entry generation from physical movements.
 *
 * This service is called internally after each MovimientoStock registration.
 * Users never interact with this directly - accounting happens automatically.
 *
 * Flow:
 * 1. Physical movement (entrada/salida) is registered
 * 2. This service is called with the MovimientoStockDto
 * 3. Finds applicable ReglaContabilizacion
 * 4. Calculates monetary value (cantidad × precio)
 * 5. Generates AsientoContable with LineaAsiento entries
 */
public interface IContabilizacionAutomaticaService {

    /**
     * Generates an accounting entry from a physical stock movement.
     * Called automatically after MovimientoStock registration.
     *
     * @param movimiento The stock movement that triggered the entry
     * @return The generated accounting entry, or empty if no rule applies
     */
    Optional<AsientoContableDto> generarAsientoDesdeMovimiento(MovimientoStockDto movimiento);

    /**
     * Finds the applicable accounting rule for a movement type.
     * Rules are matched by priority (lower = higher priority).
     *
     * @param tipoMovimiento The type of stock movement
     * @param fincaId Optional farm ID for farm-specific rules
     * @param almacenId Optional warehouse ID for warehouse-specific rules
     * @param tipoProducto Optional product type for product-specific rules
     * @return The most applicable rule, or empty if none found
     */
    Optional<ReglaContabilizacionDto> findReglaAplicable(
            TipoMovimientoStock tipoMovimiento,
            UUID fincaId,
            UUID almacenId,
            String tipoProducto);

    /**
     * Gets all active accounting rules.
     */
    List<ReglaContabilizacionDto> findAllReglas();

    /**
     * Gets all rules for a specific movement type.
     */
    List<ReglaContabilizacionDto> findReglasByTipoMovimiento(TipoMovimientoStock tipo);

    /**
     * Gets an accounting entry by its linked stock movement.
     */
    Optional<AsientoContableDto> findAsientoByMovimientoStockId(UUID movimientoStockId);

    /**
     * Gets all accounting entries for a date range.
     */
    List<AsientoContableDto> findAsientosByFecha(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Gets total debits for a period.
     */
    BigDecimal getTotalDebitoPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Gets total credits for a period.
     */
    BigDecimal getTotalCreditoPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Finds entries that are not balanced (DEBE != HABER).
     */
    List<AsientoContableDto> findAsientosDescuadrados();

    /**
     * Checks if an entry already exists for a stock movement.
     * Used to prevent duplicate entries.
     */
    boolean existeAsientoParaMovimiento(UUID movimientoStockId);

    /**
     * Creates a new accounting rule.
     */
    ReglaContabilizacionDto crearRegla(ReglaContabilizacionDto regla);

    /**
     * Activates an accounting rule.
     */
    void activarRegla(UUID reglaId);

    /**
     * Deactivates an accounting rule.
     */
    void desactivarRegla(UUID reglaId);
}
