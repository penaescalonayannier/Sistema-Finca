package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.AsientoContableDto;
import com.kynsoft.report.domain.dto.CuentaContableDto;
import com.kynsoft.report.domain.dto.MovimientoStockDto;
import com.kynsoft.report.domain.dto.ProductoDto;
import com.kynsoft.report.domain.dto.ReglaContabilizacionDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.services.IContabilizacionAutomaticaService;
import com.kynsoft.report.domain.services.ICuentaContableService;
import com.kynsoft.report.domain.services.IProductoService;
import com.kynsoft.report.infrastructure.entity.AsientoContable;
import com.kynsoft.report.infrastructure.entity.LineaAsiento;
import com.kynsoft.report.infrastructure.entity.ReglaContabilizacion;
import com.kynsoft.report.infrastructure.repository.command.AsientoContableWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.ReglaContabilizacionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AsientoContableReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ReglaContabilizacionReadDataJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of automatic accounting entry generation.
 *
 * This service is called internally after each MovimientoStock registration.
 * It generates double-entry accounting records following Cuban accounting standards.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContabilizacionAutomaticaServiceImpl implements IContabilizacionAutomaticaService {

    private final ReglaContabilizacionReadDataJPARepository reglaReadRepository;
    private final ReglaContabilizacionWriteDataJPARepository reglaWriteRepository;
    private final AsientoContableReadDataJPARepository asientoReadRepository;
    private final AsientoContableWriteDataJPARepository asientoWriteRepository;
    private final IProductoService productoService;
    private final ICuentaContableService cuentaContableService;

    @Override
    @Transactional
    public Optional<AsientoContableDto> generarAsientoDesdeMovimiento(MovimientoStockDto movimiento) {
        log.debug("Generating accounting entry for movement: {} type: {}",
                movimiento.getId(), movimiento.getTipo());

        // Check if entry already exists for this movement
        if (existeAsientoParaMovimiento(movimiento.getId())) {
            log.warn("Accounting entry already exists for movement: {}", movimiento.getId());
            return asientoReadRepository.findByMovimientoStockId(movimiento.getId())
                    .map(AsientoContable::toAggregate);
        }

        // Find applicable accounting rule
        ProductoDto producto = productoService.findById(movimiento.getProductoId());
        String tipoProducto = producto.getTipoProducto() != null
                ? producto.getTipoProducto().name()
                : null;

        Optional<ReglaContabilizacionDto> reglaOpt = findReglaAplicable(
                movimiento.getTipo(),
                movimiento.getFincaId(),
                movimiento.getAlmacenId(),
                tipoProducto);

        if (reglaOpt.isEmpty()) {
            log.warn("No accounting rule found for movement type: {}", movimiento.getTipo());
            return Optional.empty();
        }

        ReglaContabilizacionDto regla = reglaOpt.get();

        // Calculate monetary value
        BigDecimal precio = determinarPrecio(movimiento, producto);
        BigDecimal importe = precio.multiply(BigDecimal.valueOf(movimiento.getCantidad()));

        if (importe.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Calculated amount is zero or negative for movement: {}", movimiento.getId());
            return Optional.empty();
        }

        // El consecutivo se reserva en PostgreSQL bajo bloqueo de fila. No se
        // calcula con MAX en memoria porque dos operaciones concurrentes podían
        // recibir el mismo AS-AAAAMMDD-NNNN.
        LocalDate fechaAsiento = movimiento.getFecha() != null
                ? movimiento.getFecha().toLocalDate()
                : LocalDate.now();
        String numeroAsiento = asientoWriteRepository.reservarSiguienteNumero(fechaAsiento);

        // Build description from template or default
        String descripcion = buildDescripcion(regla, movimiento, producto);

        // Get account names
        String nombreCuentaDebito = obtenerNombreCuenta(regla.getCuentaDebito());
        String nombreCuentaCredito = obtenerNombreCuenta(regla.getCuentaCredito());

        // Create the accounting entry
        AsientoContable asiento = new AsientoContable();
        asiento.setId(UUID.randomUUID());
        asiento.setNumero(numeroAsiento);
        asiento.setFecha(fechaAsiento);
        asiento.setDescripcion(descripcion);
        asiento.setMovimientoStockId(movimiento.getId());
        asiento.setTablaOrigen("movimiento_stock");
        asiento.setReglaId(regla.getId());
        asiento.setAsentado(true);
        asiento.setFechaAsentado(LocalDateTime.now());
        asiento.setUsuarioAsento("SISTEMA");

        // Determine cost center: prefer movement's value, fallback to rule
        String centroCostoCredito = movimiento.getCentroCosto() != null && !movimiento.getCentroCosto().isEmpty()
                ? movimiento.getCentroCosto()
                : regla.getCentroCostoCredito();

        // Create debit line
        LineaAsiento lineaDebito = LineaAsiento.crearDebito(
                regla.getCuentaDebito(),
                nombreCuentaDebito,
                importe,
                descripcion,
                regla.getCentroCostoDebito());
        lineaDebito.setOrden(1);

        // Create credit line (uses movement's centro de costo when available)
        LineaAsiento lineaCredito = LineaAsiento.crearCredito(
                regla.getCuentaCredito(),
                nombreCuentaCredito,
                importe,
                descripcion,
                centroCostoCredito);
        lineaCredito.setOrden(2);

        // Add lines to entry
        asiento.addLinea(lineaDebito);
        asiento.addLinea(lineaCredito);

        // Verify entry is balanced
        if (!asiento.estaCuadrado()) {
            log.error("Generated entry is not balanced! DEBE={} HABER={}",
                    asiento.getTotalDebe(), asiento.getTotalHaber());
            throw new IllegalStateException("Generated accounting entry is not balanced");
        }

        // Save
        AsientoContable saved = asientoWriteRepository.save(asiento);
        log.info("Generated accounting entry {} for movement {} amount {}",
                numeroAsiento, movimiento.getId(), importe);

        return Optional.of(saved.toAggregate());
    }

    /**
     * Determines the price to use based on movement type.
     * - SALIDA_AUTOCONSUMO (workers): priceTrabajador
     * - SALIDA_COMEDOR (cafeteria): priceComedor
     * - Other: general price
     */
    private BigDecimal determinarPrecio(MovimientoStockDto movimiento, ProductoDto producto) {
        TipoMovimientoStock tipo = movimiento.getTipo();

        // For salidas to workers
        if (tipo == TipoMovimientoStock.SALIDA_AUTOCONSUMO) {
            return BigDecimal.valueOf(producto.getPriceTrabajador());
        }

        // For salidas to cafeteria
        if (tipo == TipoMovimientoStock.SALIDA_COMEDOR) {
            return BigDecimal.valueOf(producto.getPriceComedor());
        }

        // Default: general price (SALIDA_VENTA, entries, etc.)
        return BigDecimal.valueOf(producto.getPrice());
    }

    private String buildDescripcion(ReglaContabilizacionDto regla, MovimientoStockDto mov, ProductoDto producto) {
        String template = regla.getDescripcionPlantilla();
        if (template == null || template.isEmpty()) {
            return String.format("%s - %s x %d",
                    mov.getTipo().name(),
                    producto.getName(),
                    mov.getCantidad());
        }

        return template
                .replace("{producto}", producto.getName())
                .replace("{cantidad}", String.valueOf(mov.getCantidad()))
                .replace("{tipo}", mov.getTipo().name());
    }

    private String obtenerNombreCuenta(String codigoCuenta) {
        return cuentaContableService.findByCodigoOptional(codigoCuenta)
                .map(CuentaContableDto::getNombre)
                .orElseGet(() -> {
                    log.warn("Could not find account name for: {}", codigoCuenta);
                    return codigoCuenta;
                });
    }

    @Override
    public Optional<ReglaContabilizacionDto> findReglaAplicable(
            TipoMovimientoStock tipoMovimiento,
            UUID fincaId,
            UUID almacenId,
            String tipoProducto) {

        List<ReglaContabilizacion> reglas = reglaReadRepository.findReglasAplicables(
                tipoMovimiento, fincaId, almacenId, tipoProducto);

        if (reglas.isEmpty()) {
            // Try with generic rule (no specific filters)
            return reglaReadRepository
                    .findFirstByTipoMovimientoAndActivoTrueOrderByPrioridadAsc(tipoMovimiento)
                    .map(ReglaContabilizacion::toAggregate);
        }

        // Return the first (highest priority)
        return Optional.of(reglas.get(0).toAggregate());
    }

    @Override
    public List<ReglaContabilizacionDto> findAllReglas() {
        return reglaReadRepository.findByActivoTrue()
                .stream()
                .map(ReglaContabilizacion::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReglaContabilizacionDto> findReglasByTipoMovimiento(TipoMovimientoStock tipo) {
        return reglaReadRepository.findByTipoMovimientoAndActivoTrue(tipo)
                .stream()
                .map(ReglaContabilizacion::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AsientoContableDto> findAsientoByMovimientoStockId(UUID movimientoStockId) {
        return asientoReadRepository.findByMovimientoStockIdWithLineas(movimientoStockId)
                .map(AsientoContable::toAggregate);
    }

    @Override
    public List<AsientoContableDto> findAsientosByFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        return asientoReadRepository.findByFechaBetween(fechaInicio, fechaFin)
                .stream()
                .map(AsientoContable::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalDebitoPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        BigDecimal total = asientoReadRepository.sumTotalDebePorPeriodo(fechaInicio, fechaFin);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalCreditoPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        BigDecimal total = asientoReadRepository.sumTotalHaberPorPeriodo(fechaInicio, fechaFin);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public List<AsientoContableDto> findAsientosDescuadrados() {
        return asientoReadRepository.findAsientosDescuadrados()
                .stream()
                .map(AsientoContable::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existeAsientoParaMovimiento(UUID movimientoStockId) {
        return asientoReadRepository.existsByMovimientoStockId(movimientoStockId);
    }

    @Override
    @Transactional
    public ReglaContabilizacionDto crearRegla(ReglaContabilizacionDto regla) {
        ReglaContabilizacion entity = new ReglaContabilizacion(regla);
        ReglaContabilizacion saved = reglaWriteRepository.save(entity);
        log.info("Created accounting rule for movement type: {}", regla.getTipoMovimiento());
        return saved.toAggregate();
    }

    @Override
    @Transactional
    public void activarRegla(UUID reglaId) {
        reglaWriteRepository.activar(reglaId);
        log.info("Activated accounting rule: {}", reglaId);
    }

    @Override
    @Transactional
    public void desactivarRegla(UUID reglaId) {
        reglaWriteRepository.desactivar(reglaId);
        log.info("Deactivated accounting rule: {}", reglaId);
    }
}
