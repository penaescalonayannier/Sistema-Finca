package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.AlmacenFincaProductoResponse;
import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.dto.MovimientoStockDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.services.IAlmacenFincaProductoService;
import com.kynsoft.report.domain.services.IMovimientoStockService;
import com.kynsoft.report.infrastructure.entity.Almacen;
import com.kynsoft.report.infrastructure.entity.AlmacenFincaProducto;
import com.kynsoft.report.infrastructure.entity.FincaProducto;
import com.kynsoft.report.infrastructure.repository.command.AlmacenFincaProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.FincaProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AlmacenFincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AlmacenReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaProductoReadDataJPARepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class AlmacenFincaProductoServiceImpl implements IAlmacenFincaProductoService {

    private final AlmacenFincaProductoWriteDataJPARepository repositoryCommand;
    private final AlmacenFincaProductoReadDataJPARepository repositoryQuery;
    private final AlmacenReadDataJPARepository almacenRepository;
    private final FincaProductoReadDataJPARepository fincaProductoRepository;
    private final FincaProductoWriteDataJPARepository fincaProductoWriteRepository;
    private final IMovimientoStockService movimientoStockService;

    public AlmacenFincaProductoServiceImpl(
            AlmacenFincaProductoWriteDataJPARepository repositoryCommand,
            AlmacenFincaProductoReadDataJPARepository repositoryQuery,
            AlmacenReadDataJPARepository almacenRepository,
            FincaProductoReadDataJPARepository fincaProductoRepository,
            FincaProductoWriteDataJPARepository fincaProductoWriteRepository,
            IMovimientoStockService movimientoStockService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.almacenRepository = almacenRepository;
        this.fincaProductoRepository = fincaProductoRepository;
        this.fincaProductoWriteRepository = fincaProductoWriteRepository;
        this.movimientoStockService = movimientoStockService;
    }

    // ==================== CRUD ====================

    @Override
    public UUID asignarProducto(UUID almacenId, UUID fincaProductoId, Double stockInicial,
                                 Double stockMinimo, Double stockMaximo) {
        Almacen almacen = almacenRepository.findById(almacenId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("almacenId", "Almacén no encontrado."))));
        if (!Boolean.TRUE.equals(almacen.getActivo())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("almacenId", "No se puede asignar productos a un almacén inactivo.")));
        }

        FincaProducto fincaProducto = fincaProductoRepository.findById(fincaProductoId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("fincaProductoId", "Producto no encontrado."))));

        if (!almacen.getFinca().getId().equals(fincaProducto.getFinca().getId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("fincaProductoId", "El producto pertenece a otra finca.")));
        }
        if (!Boolean.TRUE.equals(fincaProducto.getActivo())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("fincaProductoId", "No se puede asignar un producto de finca inactivo.")));
        }
        validarStockNoNegativo(stockInicial, "stockInicial");
        validarLimites(stockMinimo, stockMaximo);

        if (repositoryQuery.existsByAlmacenIdAndFincaProductoIdAndActivoTrue(almacenId, fincaProductoId)) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("fincaProductoId", "Producto ya existe en este almacén.")));
        }

        AlmacenFincaProducto afp = new AlmacenFincaProducto();
        afp.setId(UUID.randomUUID());
        afp.setAlmacen(almacen);
        afp.setFincaProducto(fincaProducto);
        afp.setStock(stockInicial != null ? stockInicial : 0.0);
        afp.setStockMinimo(stockMinimo != null ? stockMinimo : 0.0);
        afp.setStockMaximo(stockMaximo);
        afp.setActivo(true);

        repositoryCommand.save(afp);

        if (stockInicial != null && stockInicial > 0) {
            // El stock inicial representa una existencia física incorporada a
            // este almacén. Por tanto también debe incorporarse al saldo de la
            // finca; omitirlo dejaba ambos inventarios desincronizados.
            Double stockFincaAnterior = fincaProducto.getStock() != null ? fincaProducto.getStock() : 0.0;
            fincaProducto.setStock(stockFincaAnterior + stockInicial);
            fincaProductoWriteRepository.save(fincaProducto);
            registrarMovimiento(afp, TipoMovimientoStock.ENTRADA_AJUSTE, stockInicial,
                    0.0, stockInicial, "Stock inicial al asignar producto");
        }

        return afp.getId();
    }

    @Override
    public void actualizarStock(UUID id, Double nuevoStock) {
        validarStockNoNegativo(nuevoStock, "nuevoStock");
        AlmacenFincaProducto afp = findEntityById(id);
        Double stockAnterior = afp.getStock();
        afp.setStock(nuevoStock);
        repositoryCommand.save(afp);

        double diferencia = nuevoStock - stockAnterior;

        // Actualizar también el stock total de FincaProducto
        FincaProducto fp = afp.getFincaProducto();
        Double stockFincaAnterior = fp.getStock() != null ? fp.getStock() : 0.0;
        Double stockFincaNuevo = stockFincaAnterior + diferencia;
        if (stockFincaNuevo < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("nuevoStock", "El ajuste dejaría el stock de la finca en negativo. "
                            + "Stock actual: " + stockFincaAnterior)));
        }
        fp.setStock(stockFincaNuevo);
        fincaProductoWriteRepository.save(fp);

        TipoMovimientoStock tipo = diferencia > 0 ? TipoMovimientoStock.ENTRADA_AJUSTE : TipoMovimientoStock.SALIDA_AJUSTE;
        registrarMovimiento(afp, tipo, Math.abs(diferencia), stockAnterior, nuevoStock, "Ajuste manual de stock");
    }

    @Override
    public void actualizarLimites(UUID id, Double stockMinimo, Double stockMaximo) {
        validarLimites(stockMinimo, stockMaximo);
        AlmacenFincaProducto afp = findEntityById(id);
        if (stockMinimo != null) afp.setStockMinimo(stockMinimo);
        if (stockMaximo != null) afp.setStockMaximo(stockMaximo);
        repositoryCommand.save(afp);
    }

    @Override
    public void removerProducto(UUID id) {
        AlmacenFincaProducto afp = findEntityById(id);

        if (afp.getStock() > 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "No se puede remover producto con stock. Transfiera o ajuste a 0 primero.")));
        }

        afp.setActivo(false);
        repositoryCommand.save(afp);
    }

    @Override
    public void reactivarProducto(UUID id) {
        AlmacenFincaProducto afp = repositoryQuery.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Producto-Almacén no encontrado."))));

        if (afp.getActivo()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Producto ya está activo.")));
        }

        afp.setActivo(true);
        repositoryCommand.save(afp);
    }

    // ==================== ENTRADAS ====================

    @Override
    public void entradaProduccion(UUID almacenFincaProductoId, Double cantidad, String descripcion) {
        entrada(almacenFincaProductoId, cantidad, TipoMovimientoStock.ENTRADA_PRODUCCION, descripcion, null);
    }

    @Override
    public void entradaProduccion(UUID almacenFincaProductoId, Double cantidad, String descripcion, String centroCosto) {
        entrada(almacenFincaProductoId, cantidad, TipoMovimientoStock.ENTRADA_PRODUCCION, descripcion, centroCosto);
    }

    @Override
    public void entradaFactura(UUID almacenFincaProductoId, Double cantidad, String numeroFactura, String descripcion) {
        String desc = "Factura: " + numeroFactura + (descripcion != null ? " - " + descripcion : "");
        entrada(almacenFincaProductoId, cantidad, TipoMovimientoStock.ENTRADA_FACTURA, desc, null);
    }

    @Override
    public void entradaConduce(UUID almacenFincaProductoId, Double cantidad, String observaciones) {
        entrada(almacenFincaProductoId, cantidad, TipoMovimientoStock.ENTRADA_CONDUCE, observaciones, null);
    }

    @Override
    public void entrada(UUID almacenFincaProductoId, Double cantidad, TipoMovimientoStock tipo, String descripcion) {
        entrada(almacenFincaProductoId, cantidad, tipo, descripcion, null);
    }

    @Override
    public void entrada(UUID almacenFincaProductoId, Double cantidad, TipoMovimientoStock tipo, String descripcion, String centroCosto) {
        validarCantidadPositiva(cantidad);
        AlmacenFincaProducto afp = findEntityById(almacenFincaProductoId);

        Double stockAnterior = afp.getStock();
        Double stockNuevo = stockAnterior + cantidad;
        afp.setStock(stockNuevo);
        repositoryCommand.save(afp);

        // Actualizar también el stock total de FincaProducto
        FincaProducto fp = afp.getFincaProducto();
        fp.setStock(fp.getStock() + cantidad);
        fincaProductoWriteRepository.save(fp);

        registrarMovimiento(afp, tipo, cantidad, stockAnterior, stockNuevo, descripcion, centroCosto);
        log.info("Entrada registrada: almacén={}, producto={}, cantidad={}, stockNuevo={}, stockFinca={}, centroCosto={}",
                afp.getAlmacen().getNombre(), fp.getProducto().getName(), cantidad, stockNuevo, fp.getStock(), centroCosto);
    }

    /**
     * Variante documental de la entrada. No reutiliza la entrada genérica para
     * evitar que el movimiento quede sin el vínculo bidireccional exigido por
     * el expediente de recepción SC-2-04.
     */
    @Override
    public UUID entradaConInformeRecepcion(UUID almacenFincaProductoId, Double cantidad,
                                           TipoMovimientoStock tipo, UUID informeRecepcionId, UUID movimientoStockId,
                                           String descripcion) {
        validarCantidadPositiva(cantidad);
        if (informeRecepcionId == null || (tipo != TipoMovimientoStock.ENTRADA_FACTURA
                && tipo != TipoMovimientoStock.ENTRADA_CONDUCE)) {
            throw new IllegalArgumentException("La entrada documental debe indicar un informe de recepción y su fuente.");
        }
        AlmacenFincaProducto afp = findEntityById(almacenFincaProductoId);
        Double stockAnterior = afp.getStock();
        Double stockNuevo = stockAnterior + cantidad;
        afp.setStock(stockNuevo);
        repositoryCommand.save(afp);
        FincaProducto fp = afp.getFincaProducto();
        fp.setStock(fp.getStock() + cantidad);
        fincaProductoWriteRepository.save(fp);

        UUID movimientoId = movimientoStockId == null ? UUID.randomUUID() : movimientoStockId;
        MovimientoStockDto movimiento = MovimientoStockDto.builder()
                .id(movimientoId)
                .fincaProductoId(fp.getId())
                .fincaId(fp.getFinca().getId())
                .productoId(fp.getProducto().getId())
                .almacenId(afp.getAlmacen().getId())
                .tipo(tipo).cantidad(cantidad).stockAnterior(stockAnterior).stockNuevo(stockNuevo)
                .referenciaId(informeRecepcionId).referenciaTabla("informe_recepcion")
                .descripcion(descripcion).build();
        movimientoStockService.registrar(movimiento);
        return movimientoId;
    }

    // ==================== PRODUCCIÓN TERMINADA ====================

    /**
     * Esta operación no delega en {@link #entrada} porque la producción
     * terminada debe dejar un solo MovimientoStock, con la referencia del
     * documento, y debe afectar el almacén y FincaProducto exactamente una
     * vez dentro de la misma transacción.
     */
    @Override
    public void registrarEntradaProduccionTerminada(UUID almacenFincaProductoId, Double cantidad,
                                                     UUID produccionId, String descripcion,
                                                     String centroCosto) {
        validarCantidadPositiva(cantidad);
        validarReferenciaProduccion(produccionId);
        modificarStockPorProduccion(almacenFincaProductoId, cantidad,
                TipoMovimientoStock.ENTRADA_PRODUCCION, produccionId, descripcion, centroCosto);
    }

    @Override
    public void actualizarEntradaProduccion(UUID almacenFincaProductoId, Double cantidadAnterior,
                                            Double cantidadNueva, String descripcion) {
        actualizarEntradaProduccion(almacenFincaProductoId, cantidadAnterior, cantidadNueva,
                null, descripcion, null);
    }

    @Override
    public void actualizarEntradaProduccion(UUID almacenFincaProductoId, Double cantidadAnterior,
                                            Double cantidadNueva, UUID produccionId, String descripcion) {
        validarReferenciaProduccion(produccionId);
        actualizarEntradaProduccion(almacenFincaProductoId, cantidadAnterior, cantidadNueva,
                produccionId, descripcion, null);
    }

    @Override
    public void actualizarEntradaProduccion(UUID almacenFincaProductoId, Double cantidadAnterior,
                                            Double cantidadNueva, UUID produccionId,
                                            String descripcion, String centroCosto) {
        validarCantidadPositiva(cantidadAnterior);
        validarCantidadPositiva(cantidadNueva);

        double ajuste = cantidadNueva - cantidadAnterior;
        if (Double.compare(ajuste, 0.0) == 0) {
            return;
        }

        modificarStockPorProduccion(almacenFincaProductoId, ajuste,
                ajuste > 0 ? TipoMovimientoStock.ENTRADA_PRODUCCION : TipoMovimientoStock.AJUSTE_EDICION,
                produccionId, descripcion, centroCosto);
    }

    @Override
    public void revertirEntradaProduccion(UUID almacenFincaProductoId, Double cantidad, String descripcion) {
        revertirEntradaProduccion(almacenFincaProductoId, cantidad, null, descripcion, null);
    }

    @Override
    public void revertirEntradaProduccion(UUID almacenFincaProductoId, Double cantidad,
                                          UUID produccionId, String descripcion) {
        validarReferenciaProduccion(produccionId);
        revertirEntradaProduccion(almacenFincaProductoId, cantidad, produccionId, descripcion, null);
    }

    @Override
    public void revertirEntradaProduccion(UUID almacenFincaProductoId, Double cantidad,
                                          UUID produccionId, String descripcion, String centroCosto) {
        validarCantidadPositiva(cantidad);
        modificarStockPorProduccion(almacenFincaProductoId, -cantidad,
                TipoMovimientoStock.REVERSION_PRODUCCION, produccionId, descripcion, centroCosto);
    }

    // ==================== SALIDAS ====================

    @Override
    public void salida(UUID almacenFincaProductoId, Double cantidad, String descripcion) {
        validarCantidadPositiva(cantidad);
        AlmacenFincaProducto afp = findEntityById(almacenFincaProductoId);

        Double stockAnterior = afp.getStock();
        if (stockAnterior < cantidad) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidad", "Stock insuficiente. Disponible: " + stockAnterior)));
        }

        Double stockNuevo = stockAnterior - cantidad;
        afp.setStock(stockNuevo);
        repositoryCommand.save(afp);

        // Actualizar también el stock total de FincaProducto
        FincaProducto fp = afp.getFincaProducto();
        fp.setStock(Math.max(0.0, fp.getStock() - cantidad));
        fincaProductoWriteRepository.save(fp);

        registrarMovimiento(afp, TipoMovimientoStock.SALIDA_AUTOCONSUMO, cantidad, stockAnterior, stockNuevo, descripcion);
        log.info("Salida registrada: almacén={}, producto={}, cantidad={}, stockNuevo={}, stockFinca={}",
                afp.getAlmacen().getNombre(), fp.getProducto().getName(), cantidad, stockNuevo, fp.getStock());
    }

    @Override
    public void salidaTrabajador(UUID almacenFincaProductoId, Double cantidad, UUID trabajadorId, String descripcion) {
        salida(almacenFincaProductoId, cantidad, "Trabajador: " + trabajadorId + " - " + descripcion);
    }

    @Override
    public void salidaComedor(UUID almacenFincaProductoId, Double cantidad, String descripcion) {
        salida(almacenFincaProductoId, cantidad, "Comedor - " + descripcion);
    }

    // ==================== TRANSFERENCIAS ====================

    @Override
    public void transferir(UUID origenId, UUID destinoAlmacenId, Double cantidad, String observaciones) {
        validarCantidadPositiva(cantidad);
        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("observaciones", "La transferencia debe indicar su referencia o motivo.")));
        }
        AlmacenFincaProducto origen = findEntityById(origenId);

        if (!Boolean.TRUE.equals(origen.getAlmacen().getActivo())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("origenId", "No se puede transferir desde un almacén inactivo.")));
        }

        if (origen.getStock() < cantidad) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidad", "Stock insuficiente para transferir. Disponible: " + origen.getStock())));
        }

        Almacen almacenDestino = almacenRepository.findById(destinoAlmacenId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("destinoAlmacenId", "Almacén destino no encontrado."))));

        if (!almacenDestino.getActivo()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("destinoAlmacenId", "Almacén destino está inactivo.")));
        }

        if (origen.getAlmacen().getId().equals(destinoAlmacenId)) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("destinoAlmacenId", "El almacén destino debe ser diferente al almacén origen.")));
        }

        if (!almacenDestino.getFinca().getId().equals(origen.getAlmacen().getFinca().getId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("destinoAlmacenId", "Solo se puede transferir entre almacenes de la misma finca.")));
        }

        AlmacenFincaProducto destino = repositoryQuery
                .findByAlmacenIdAndFincaProductoIdAndActivoTrue(destinoAlmacenId, origen.getFincaProducto().getId())
                .orElseGet(() -> {
                    AlmacenFincaProducto nuevo = new AlmacenFincaProducto();
                    nuevo.setId(UUID.randomUUID());
                    nuevo.setAlmacen(almacenDestino);
                    nuevo.setFincaProducto(origen.getFincaProducto());
                    nuevo.setStock(0.0);
                    nuevo.setStockMinimo(0.0);
                    nuevo.setActivo(true);
                    return repositoryCommand.save(nuevo);
                });

        Double stockAnteriorOrigen = origen.getStock();
        Double stockNuevoOrigen = stockAnteriorOrigen - cantidad;
        origen.setStock(stockNuevoOrigen);
        repositoryCommand.save(origen);

        Double stockAnteriorDestino = destino.getStock();
        Double stockNuevoDestino = stockAnteriorDestino + cantidad;
        destino.setStock(stockNuevoDestino);
        repositoryCommand.save(destino);

        UUID referenciaTransferencia = UUID.randomUUID();
        String desc = "Transferencia a " + almacenDestino.getNombre() + " - " + observaciones;
        registrarMovimientoTransferencia(origen, TipoMovimientoStock.TRANSFERENCIA_SALIDA, cantidad,
                stockAnteriorOrigen, stockNuevoOrigen, referenciaTransferencia, desc);

        String descDestino = "Transferencia desde " + origen.getAlmacen().getNombre() + " - " + observaciones;
        registrarMovimientoTransferencia(destino, TipoMovimientoStock.TRANSFERENCIA_ENTRADA, cantidad,
                stockAnteriorDestino, stockNuevoDestino, referenciaTransferencia, descDestino);

        log.info("Transferencia completada: {}→{}, producto={}, cantidad={}",
                origen.getAlmacen().getNombre(), almacenDestino.getNombre(),
                origen.getFincaProducto().getProducto().getName(), cantidad);
    }

    @Override
    public void transferirConDestino(UUID origenAlmacenId, UUID fincaProductoId,
                                      UUID destinoAlmacenId, Double cantidad, String observaciones) {
        AlmacenFincaProducto origen = repositoryQuery
                .findByAlmacenIdAndFincaProductoIdAndActivoTrue(origenAlmacenId, fincaProductoId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("fincaProductoId", "Producto no encontrado en almacén origen."))));

        transferir(origen.getId(), destinoAlmacenId, cantidad, observaciones);
    }

    // ==================== QUERIES ====================

    @Override
    public AlmacenFincaProductoDto findById(UUID id) {
        return findEntityById(id).toAggregate();
    }

    @Override
    public AlmacenFincaProductoDto findByAlmacenIdAndFincaProductoId(UUID almacenId, UUID fincaProductoId) {
        return repositoryQuery.findByAlmacenIdAndFincaProductoIdAndActivoTrue(almacenId, fincaProductoId)
                .map(AlmacenFincaProducto::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("fincaProductoId", "Producto no encontrado en este almacén."))));
    }

    @Override
    public List<AlmacenFincaProductoDto> findByAlmacenId(UUID almacenId) {
        return repositoryQuery.findByAlmacenIdAndActivoTrue(almacenId).stream()
                .map(AlmacenFincaProducto::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlmacenFincaProductoDto> findByFincaProductoId(UUID fincaProductoId) {
        return repositoryQuery.findByFincaProductoIdAndActivoTrue(fincaProductoId).stream()
                .map(AlmacenFincaProducto::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlmacenFincaProductoDto> findByFincaId(UUID fincaId) {
        return repositoryQuery.findByFincaIdAndActivoTrue(fincaId).stream()
                .map(AlmacenFincaProducto::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public PaginatedResponse search(UUID almacenId, Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<AlmacenFincaProducto> specs = new GenericSpecificationsBuilder<>(filterCriteria);
        Specification<AlmacenFincaProducto> almacenSpec = (root, query, cb) ->
                cb.equal(root.get("almacen").get("id"), almacenId);
        Specification<AlmacenFincaProducto> activoSpec = (root, query, cb) ->
                cb.equal(root.get("activo"), true);
        Specification<AlmacenFincaProducto> combinedSpec = Specification.where(specs).and(almacenSpec).and(activoSpec);

        Page<AlmacenFincaProducto> data = repositoryQuery.findAll(combinedSpec, pageable);
        List<AlmacenFincaProductoResponse> responses = data.getContent().stream()
                .map(afp -> new AlmacenFincaProductoResponse(afp.toAggregate()))
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    // ==================== UTILIDADES ====================

    @Override
    public Double getStockTotalAlmacen(UUID almacenId) {
        Double total = repositoryQuery.sumStockByAlmacenId(almacenId);
        return total != null ? total : 0.0;
    }

    @Override
    public Double getStockTotalProductoEnFinca(UUID fincaProductoId) {
        Double total = repositoryQuery.sumStockByFincaProductoId(fincaProductoId);
        return total != null ? total : 0.0;
    }

    @Override
    public boolean existsProductoEnAlmacen(UUID almacenId, UUID fincaProductoId) {
        return repositoryQuery.existsByAlmacenIdAndFincaProductoIdAndActivoTrue(almacenId, fincaProductoId);
    }

    @Override
    public List<AlmacenFincaProductoDto> getAlmacenesDestinoDisponibles(UUID almacenId, UUID fincaProductoId) {
        AlmacenFincaProducto origen = repositoryQuery.findByAlmacenIdAndFincaProductoIdAndActivoTrue(almacenId, fincaProductoId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("fincaProductoId", "Producto no encontrado en almacén."))));

        UUID fincaId = origen.getAlmacen().getFinca().getId();
        return repositoryQuery.findAlmacenesDestinoDisponibles(almacenId, fincaId, fincaProductoId).stream()
                .map(AlmacenFincaProducto::toAggregate)
                .collect(Collectors.toList());
    }

    // ==================== HELPERS ====================

    private AlmacenFincaProducto findEntityById(UUID id) {
        return repositoryQuery.findById(id)
                .filter(AlmacenFincaProducto::getActivo)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Producto-Almacén no encontrado."))));
    }

    private void validarCantidadPositiva(Double cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidad", "Cantidad debe ser mayor a 0.")));
        }
    }

    private void validarStockNoNegativo(Double stock, String campo) {
        if (stock != null && stock < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField(campo, "El stock no puede ser negativo.")));
        }
    }

    private void validarLimites(Double stockMinimo, Double stockMaximo) {
        validarStockNoNegativo(stockMinimo, "stockMinimo");
        validarStockNoNegativo(stockMaximo, "stockMaximo");
        if (stockMinimo != null && stockMaximo != null && stockMaximo < stockMinimo) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("stockMaximo", "El stock máximo no puede ser menor que el mínimo.")));
        }
    }

    private void validarReferenciaProduccion(UUID produccionId) {
        if (produccionId == null) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("produccionId", "La producción terminada es requerida.")));
        }
    }

    /**
     * Modifica ambos saldos de inventario y deja exactamente un movimiento.
     * El signo de {@code variacion} determina si la operación aumenta o
     * disminuye las existencias; la cantidad almacenada en el movimiento es
     * siempre positiva y el tipo expresa su dirección.
     */
    private void modificarStockPorProduccion(UUID almacenFincaProductoId, double variacion,
                                             TipoMovimientoStock tipo, UUID produccionId,
                                             String descripcion, String centroCosto) {
        AlmacenFincaProducto afp = findEntityById(almacenFincaProductoId);
        FincaProducto fincaProducto = afp.getFincaProducto();
        Double stockAlmacenAnterior = afp.getStock() != null ? afp.getStock() : 0.0;
        Double stockFincaAnterior = fincaProducto.getStock() != null ? fincaProducto.getStock() : 0.0;

        if (variacion < 0) {
            double cantidadADisminuir = Math.abs(variacion);
            if (stockAlmacenAnterior < cantidadADisminuir) {
                throw new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("cantidad", "Stock insuficiente en el almacén. Disponible: "
                                + stockAlmacenAnterior)));
            }
            if (stockFincaAnterior < cantidadADisminuir) {
                throw new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("cantidad", "Stock insuficiente en la finca. Disponible: "
                                + stockFincaAnterior)));
            }
        }

        Double stockAlmacenNuevo = stockAlmacenAnterior + variacion;
        Double stockFincaNuevo = stockFincaAnterior + variacion;
        afp.setStock(stockAlmacenNuevo);
        fincaProducto.setStock(stockFincaNuevo);
        repositoryCommand.save(afp);
        fincaProductoWriteRepository.save(fincaProducto);

        registrarMovimientoProduccion(afp, tipo, Math.abs(variacion), stockAlmacenAnterior,
                stockAlmacenNuevo, produccionId, descripcion, centroCosto);
    }

    private void registrarMovimiento(AlmacenFincaProducto afp, TipoMovimientoStock tipo,
                                      Double cantidad, Double stockAnterior, Double stockNuevo,
                                      String descripcion) {
        registrarMovimiento(afp, tipo, cantidad, stockAnterior, stockNuevo, descripcion, null);
    }

    private void registrarMovimiento(AlmacenFincaProducto afp, TipoMovimientoStock tipo,
                                      Double cantidad, Double stockAnterior, Double stockNuevo,
                                      String descripcion, String centroCosto) {
        MovimientoStockDto movimiento = MovimientoStockDto.builder()
                .fincaProductoId(afp.getFincaProducto().getId())
                .fincaId(afp.getFincaProducto().getFinca().getId())
                .productoId(afp.getFincaProducto().getProducto().getId())
                .almacenId(afp.getAlmacen().getId())
                .tipo(tipo)
                .cantidad(cantidad != null ? cantidad : 0.0)
                .stockAnterior(stockAnterior)
                .stockNuevo(stockNuevo)
                .descripcion(descripcion)
                .centroCosto(centroCosto)
                .build();
        movimientoStockService.registrar(movimiento);
    }

    private void registrarMovimientoProduccion(AlmacenFincaProducto afp, TipoMovimientoStock tipo,
                                                Double cantidad, Double stockAnterior, Double stockNuevo,
                                                UUID produccionId, String descripcion, String centroCosto) {
        MovimientoStockDto movimiento = MovimientoStockDto.builder()
                .id(UUID.randomUUID())
                .fincaProductoId(afp.getFincaProducto().getId())
                .fincaId(afp.getFincaProducto().getFinca().getId())
                .productoId(afp.getFincaProducto().getProducto().getId())
                .almacenId(afp.getAlmacen().getId())
                .tipo(tipo)
                .cantidad(cantidad)
                .stockAnterior(stockAnterior)
                .stockNuevo(stockNuevo)
                .referenciaId(produccionId)
                .referenciaTabla(produccionId != null ? "produccion_terminada" : null)
                .descripcion(descripcion)
                .centroCosto(centroCosto)
                .build();
        movimientoStockService.registrar(movimiento);
    }

    private void registrarMovimientoTransferencia(AlmacenFincaProducto afp, TipoMovimientoStock tipo,
                                                   Double cantidad, Double stockAnterior, Double stockNuevo,
                                                   UUID transferenciaId, String descripcion) {
        MovimientoStockDto movimiento = MovimientoStockDto.builder()
                .id(UUID.randomUUID())
                .fincaProductoId(afp.getFincaProducto().getId())
                .fincaId(afp.getFincaProducto().getFinca().getId())
                .productoId(afp.getFincaProducto().getProducto().getId())
                .almacenId(afp.getAlmacen().getId())
                .tipo(tipo)
                .cantidad(cantidad)
                .stockAnterior(stockAnterior)
                .stockNuevo(stockNuevo)
                .referenciaId(transferenciaId)
                .referenciaTabla("transferencia_almacen")
                .descripcion(descripcion)
                .build();
        movimientoStockService.registrar(movimiento);
    }

}
