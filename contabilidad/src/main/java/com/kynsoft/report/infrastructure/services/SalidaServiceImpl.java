package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.SalidaResponse;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDetalleDto;
import com.kynsoft.report.domain.dto.DestinoSalida;
import com.kynsoft.report.domain.dto.ItemSalidaDto;
import com.kynsoft.report.domain.dto.LineaSalidaMultipleAlmacenDto;
import com.kynsoft.report.domain.dto.SalidaDto;
import com.kynsoft.report.domain.dto.TipoMovimiento;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.dto.TipoSalida;
import com.kynsoft.report.domain.services.IDeudaTrabajadorDetalleService;
import com.kynsoft.report.domain.services.IDeudaTrabajadorService;
import com.kynsoft.report.domain.services.IMovimientoStockService;
import com.kynsoft.report.domain.services.INumeracionService;
import com.kynsoft.report.domain.dto.TipoDocumento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.kynsoft.report.domain.services.ISalidaService;
import com.kynsoft.report.infrastructure.entity.AlmacenFincaProducto;
import com.kynsoft.report.infrastructure.entity.FincaProducto;
import com.kynsoft.report.infrastructure.entity.ItemSalida;
import com.kynsoft.report.infrastructure.entity.Salida;
import com.kynsoft.report.infrastructure.repository.command.AlmacenFincaProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.ItemSalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.SalidaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.FincaProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AlmacenFincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ItemSalidaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.SalidaReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantSpecification;
import com.kynsoft.report.infrastructure.security.TenantContext;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class SalidaServiceImpl implements ISalidaService {

    private final SalidaWriteDataJPARepository repositoryCommand;
    private final SalidaReadDataJPARepository repositoryQuery;
    private final ItemSalidaWriteDataJPARepository itemRepositoryCommand;
    private final ItemSalidaReadDataJPARepository itemRepositoryQuery;
    private final FincaProductoReadDataJPARepository fincaProductoReadRepository;
    private final FincaProductoWriteDataJPARepository fincaProductoWriteRepository;
    private final AlmacenFincaProductoReadDataJPARepository almacenFincaProductoReadRepository;
    private final AlmacenFincaProductoWriteDataJPARepository almacenFincaProductoWriteRepository;
    private final IDeudaTrabajadorService deudaTrabajadorService;
    private final IDeudaTrabajadorDetalleService deudaDetalleService;
    private final IMovimientoStockService movimientoStockService;
    private final INumeracionService numeracionService;

    public SalidaServiceImpl(
            SalidaWriteDataJPARepository repositoryCommand,
            SalidaReadDataJPARepository repositoryQuery,
            ItemSalidaWriteDataJPARepository itemRepositoryCommand,
            ItemSalidaReadDataJPARepository itemRepositoryQuery,
            FincaProductoReadDataJPARepository fincaProductoReadRepository,
            FincaProductoWriteDataJPARepository fincaProductoWriteRepository,
            AlmacenFincaProductoReadDataJPARepository almacenFincaProductoReadRepository,
            AlmacenFincaProductoWriteDataJPARepository almacenFincaProductoWriteRepository,
            IDeudaTrabajadorService deudaTrabajadorService,
            IDeudaTrabajadorDetalleService deudaDetalleService,
            IMovimientoStockService movimientoStockService,
            INumeracionService numeracionService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.itemRepositoryCommand = itemRepositoryCommand;
        this.itemRepositoryQuery = itemRepositoryQuery;
        this.fincaProductoReadRepository = fincaProductoReadRepository;
        this.fincaProductoWriteRepository = fincaProductoWriteRepository;
        this.almacenFincaProductoReadRepository = almacenFincaProductoReadRepository;
        this.almacenFincaProductoWriteRepository = almacenFincaProductoWriteRepository;
        this.deudaTrabajadorService = deudaTrabajadorService;
        this.deudaDetalleService = deudaDetalleService;
        this.movimientoStockService = movimientoStockService;
        this.numeracionService = numeracionService;
    }

    @Override
    public UUID create(SalidaDto dto, List<ItemSalidaDto> items) {
        // La entidad Salida usa identificadores UUID asignados por la aplicación.
        // Las salidas creadas fuera del comando estándar (por ejemplo, una salida múltiple)
        // también deben recibirlo antes de persistir.
        if (dto.getId() == null) {
            dto.setId(UUID.randomUUID());
        }

        // Validar que existe el FincaProducto (con producto cargado para obtener precios)
        FincaProducto fincaProducto = fincaProductoReadRepository.findByIdWithDetails(dto.getFincaProductoId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("fincaProductoId", "No se encontró la relación Finca-Producto."))));

        // Forzar inicialización del producto para obtener precios
        Hibernate.initialize(fincaProducto.getProducto());

        // Calcular cantidad total
        double cantidadTotal = items.stream().mapToDouble(ItemSalidaDto::getCantidad).sum();

        // Validar stock suficiente
        if (fincaProducto.getStock() < cantidadTotal) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidad", "Stock insuficiente. Stock actual: " + fincaProducto.getStock() + ", Cantidad solicitada: " + cantidadTotal)));
        }

        // RN-09: Determinar tipo automáticamente según destino
        TipoSalida tipoFinal = determinarTipoSegunDestino(dto.getDestino());
        dto.setTipo(tipoFinal);

        // Generar número automáticamente usando el servicio de numeración
        UUID fincaId = fincaProducto.getFinca().getId();
        TipoDocumento tipoDoc = TipoDocumento.fromTipoSalida(tipoFinal);
        dto.setNumero(numeracionService.generarSiguienteNumero(fincaId, tipoDoc));

        // Crear la salida
        Salida salida = new Salida(dto);
        repositoryCommand.save(salida);

        // Obtener el precio según el destino
        Double precio = obtenerPrecioSegunDestino(fincaProducto, dto.getDestino());

        // Crear los items y actualizar deudas si es para trabajadores
        for (ItemSalidaDto itemDto : items) {
            itemDto.setId(UUID.randomUUID());
            itemDto.setSalidaId(salida.getId());
            itemDto.setPrecio(precio);
            ItemSalida item = new ItemSalida(itemDto);
            itemRepositoryCommand.save(item);

            // Registrar para trabajadores (con o sin deuda según si pagó)
            if (dto.getDestino() == DestinoSalida.TRABAJADORES && itemDto.getTrabajadorId() != null) {
                Double valorItem = itemDto.getCantidad() * precio;
                Boolean yaPago = itemDto.getPagado() != null && itemDto.getPagado();

                // Solo incrementar deuda si NO ha pagado
                if (!yaPago) {
                    deudaTrabajadorService.incrementarDeuda(itemDto.getTrabajadorId(), valorItem);
                }

                // Siempre registrar detalle de auditoria (pagado o no)
                DeudaTrabajadorDetalleDto detalle = DeudaTrabajadorDetalleDto.builder()
                        .id(UUID.randomUUID())
                        .trabajadorId(itemDto.getTrabajadorId())
                        .salidaId(salida.getId())
                        .salidaNumero(dto.getNumero())
                        .salidaTipo(dto.getTipo())
                        .productoId(fincaProducto.getProducto().getId())
                        .productoCodigo(fincaProducto.getProducto().getCode())
                        .productoNombre(fincaProducto.getProducto().getName())
                        .cantidad(itemDto.getCantidad())
                        .precioUnitario(precio)
                        .importe(valorItem)
                        .fecha(LocalDateTime.now())
                        .activo(true)
                        .pagado(yaPago)
                        .tipoMovimiento(TipoMovimiento.COMPRA)
                        .build();
                deudaDetalleService.registrar(detalle);
            }
        }

        // Rebajar el stock con auditoría
        Double stockAnterior = fincaProducto.getStock();
        Double stockNuevo = stockAnterior - cantidadTotal;
        fincaProducto.setStock(stockNuevo);
        fincaProductoWriteRepository.save(fincaProducto);

        // Si viene de un almacén, también rebajar el stock del AlmacenFincaProducto
        UUID almacenMovimientoId = null;
        if (dto.getAlmacenFincaProductoId() != null) {
            AlmacenFincaProducto afp = almacenFincaProductoReadRepository.findById(dto.getAlmacenFincaProductoId())
                    .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("almacenFincaProductoId", "No se encontró el producto en el almacén."))));
            double stockAlmacen = afp.getStock() == null ? 0.0 : afp.getStock();
            if (!Boolean.TRUE.equals(afp.getActivo()) || !afp.getFincaProducto().getId().equals(dto.getFincaProductoId())) {
                throw new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("almacenFincaProductoId", "El producto no está activo en el almacén indicado.")));
            }
            if (stockAlmacen < cantidadTotal) {
                throw new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("cantidad", "Stock insuficiente en el almacén. Stock actual: " + stockAlmacen
                                + ", Cantidad solicitada: " + cantidadTotal)));
            }
            afp.setStock(stockAlmacen - cantidadTotal);
            almacenFincaProductoWriteRepository.save(afp);
            almacenMovimientoId = afp.getAlmacen().getId();
        }

        // Registrar movimiento de stock con el tipo correcto según destino
        TipoMovimientoStock tipoMovimiento = determinarTipoMovimientoSegunDestino(dto.getDestino());
        movimientoStockService.registrarMovimiento(
                fincaProducto.getId(),
                fincaProducto.getFinca().getId(),
                fincaProducto.getProducto().getId(),
                tipoMovimiento,
                -cantidadTotal,
                stockAnterior,
                stockNuevo,
                salida.getId(),
                "salida",
                "Salida " + dto.getNumero() + " - " + dto.getDestino(),
                almacenMovimientoId
        );

        return salida.getId();
    }

    /** Registra varias líneas de producto en un único vale o factura. */
    @Override
    @Transactional
    public List<UUID> createMultipleFromAlmacen(UUID almacenId, DestinoSalida destino, String observaciones,
                                                List<LineaSalidaMultipleAlmacenDto> lineas) {
        if (almacenId == null) {
            throw validationError("almacenId", "Debe indicar el almacén de origen.");
        }
        if (destino == null) {
            throw validationError("destino", "Debe seleccionar el destino de la salida.");
        }
        if (lineas == null || lineas.isEmpty()) {
            throw validationError("lineas", "Debe seleccionar al menos un producto.");
        }

        Set<UUID> productosEnSalida = new HashSet<>();
        List<AlmacenFincaProducto> productosAlmacen = new ArrayList<>();
        FincaProducto productoReferencia = null;
        for (LineaSalidaMultipleAlmacenDto linea : lineas) {
            if (linea == null || linea.getAlmacenFincaProductoId() == null) {
                throw validationError("lineas", "Cada línea debe indicar el producto del almacén.");
            }
            if (linea.getCantidad() == null || linea.getCantidad() <= 0) {
                throw validationError("cantidad", "La cantidad de cada producto debe ser mayor que cero.");
            }
            if (destino == DestinoSalida.TRABAJADORES) {
                if (linea.getItems() == null || linea.getItems().isEmpty()) {
                    throw validationError("items", "Debe indicar los trabajadores que compraron cada producto.");
                }
                Set<UUID> trabajadoresProducto = new HashSet<>();
                double cantidadAsignada = 0.0;
                for (ItemSalidaDto comprador : linea.getItems()) {
                    if (comprador == null || comprador.getTrabajadorId() == null) {
                        throw validationError("trabajadorId", "Debe seleccionar el trabajador que compró cada producto.");
                    }
                    if (comprador.getCantidad() == null || comprador.getCantidad() <= 0) {
                        throw validationError("cantidad", "La cantidad para cada trabajador debe ser mayor que cero.");
                    }
                    if (!trabajadoresProducto.add(comprador.getTrabajadorId())) {
                        throw validationError("trabajadorId", "No puede repetir un trabajador para el mismo producto.");
                    }
                    cantidadAsignada += comprador.getCantidad();
                }
                if (Double.compare(cantidadAsignada, linea.getCantidad()) != 0) {
                    throw validationError("cantidad", "La cantidad asignada a los trabajadores debe coincidir con la cantidad del producto.");
                }
            }
            if (!productosEnSalida.add(linea.getAlmacenFincaProductoId())) {
                throw validationError("lineas", "No puede repetir un producto en la misma salida múltiple.");
            }

            AlmacenFincaProducto afp = almacenFincaProductoReadRepository.findById(linea.getAlmacenFincaProductoId())
                    .orElseThrow(() -> validationError("almacenFincaProductoId", "No se encontró uno de los productos seleccionados."));
            if (!Boolean.TRUE.equals(afp.getActivo()) || !almacenId.equals(afp.getAlmacen().getId())) {
                throw validationError("almacenFincaProductoId", "Uno de los productos no pertenece al almacén seleccionado.");
            }
            double stockDisponible = afp.getStock() == null ? 0.0 : afp.getStock();
            if (stockDisponible < linea.getCantidad()) {
                throw validationError("cantidad", "Stock insuficiente de " + afp.getFincaProducto().getProducto().getName()
                        + ". Disponible: " + stockDisponible + ", solicitado: " + linea.getCantidad());
            }
            FincaProducto fincaProducto = fincaProductoReadRepository.findByIdWithDetails(afp.getFincaProducto().getId())
                    .orElseThrow(() -> validationError("fincaProductoId", "No se encontró uno de los productos seleccionados."));
            if (fincaProducto.getStock() < linea.getCantidad()) {
                throw validationError("cantidad", "Stock general insuficiente de " + fincaProducto.getProducto().getName()
                        + ". Disponible: " + fincaProducto.getStock() + ", solicitado: " + linea.getCantidad());
            }
            if (productoReferencia != null && !productoReferencia.getFinca().getId().equals(fincaProducto.getFinca().getId())) {
                throw validationError("lineas", "Todos los productos deben pertenecer a la misma finca.");
            }
            if (productoReferencia == null) productoReferencia = fincaProducto;
            productosAlmacen.add(afp);
        }

        TipoSalida tipo = determinarTipoSegunDestino(destino);
        SalidaDto salidaDto = SalidaDto.builder()
                .id(UUID.randomUUID())
                .tipo(tipo)
                .destino(destino)
                // Se conserva para compatibilidad con vales históricos y filtros por finca.
                .fincaProductoId(productoReferencia.getId())
                .numero(numeracionService.generarSiguienteNumero(productoReferencia.getFinca().getId(), TipoDocumento.fromTipoSalida(tipo)))
                .observaciones(observaciones)
                .build();
        Salida salida = new Salida(salidaDto);
        repositoryCommand.save(salida);

        for (int indice = 0; indice < lineas.size(); indice++) {
            LineaSalidaMultipleAlmacenDto linea = lineas.get(indice);
            AlmacenFincaProducto afp = productosAlmacen.get(indice);
            FincaProducto fincaProducto = fincaProductoReadRepository.findByIdWithDetails(afp.getFincaProducto().getId())
                    .orElseThrow(() -> validationError("fincaProductoId", "No se encontró uno de los productos seleccionados."));
            Hibernate.initialize(fincaProducto.getProducto());
            Double precio = obtenerPrecioSegunDestino(fincaProducto, destino);
            if (destino == DestinoSalida.TRABAJADORES) {
                for (ItemSalidaDto comprador : linea.getItems()) {
                    ItemSalidaDto itemDto = ItemSalidaDto.builder()
                            .id(UUID.randomUUID())
                            .salidaId(salida.getId())
                            .fincaProductoId(fincaProducto.getId())
                            .almacenFincaProductoId(afp.getId())
                            .trabajadorId(comprador.getTrabajadorId())
                            .cantidad(comprador.getCantidad())
                            .precio(precio)
                            .pagado(Boolean.TRUE.equals(comprador.getPagado()))
                            .build();
                    itemRepositoryCommand.save(new ItemSalida(itemDto));

                    Double valorItem = itemDto.getCantidad() * precio;
                    Boolean yaPago = Boolean.TRUE.equals(itemDto.getPagado());
                    if (!yaPago) {
                        deudaTrabajadorService.incrementarDeuda(itemDto.getTrabajadorId(), valorItem);
                    }
                    deudaDetalleService.registrar(DeudaTrabajadorDetalleDto.builder()
                            .id(UUID.randomUUID())
                            .trabajadorId(itemDto.getTrabajadorId())
                            .salidaId(salida.getId())
                            .salidaNumero(salida.getNumero())
                            .salidaTipo(salida.getTipo())
                            .productoId(fincaProducto.getProducto().getId())
                            .productoCodigo(fincaProducto.getProducto().getCode())
                            .productoNombre(fincaProducto.getProducto().getName())
                            .cantidad(itemDto.getCantidad())
                            .precioUnitario(precio)
                            .importe(valorItem)
                            .fecha(LocalDateTime.now())
                            .activo(true)
                            .pagado(yaPago)
                            .tipoMovimiento(TipoMovimiento.COMPRA)
                            .build());
                }
            } else {
                ItemSalidaDto itemDto = ItemSalidaDto.builder()
                        .id(UUID.randomUUID())
                        .salidaId(salida.getId())
                        .fincaProductoId(fincaProducto.getId())
                        .almacenFincaProductoId(afp.getId())
                        .cantidad(linea.getCantidad())
                        .precio(precio)
                        .build();
                itemRepositoryCommand.save(new ItemSalida(itemDto));
            }

            double stockAnterior = fincaProducto.getStock();
            double stockNuevo = stockAnterior - linea.getCantidad();
            fincaProducto.setStock(stockNuevo);
            fincaProductoWriteRepository.save(fincaProducto);
            afp.setStock((afp.getStock() == null ? 0.0 : afp.getStock()) - linea.getCantidad());
            almacenFincaProductoWriteRepository.save(afp);
            movimientoStockService.registrarMovimiento(
                    fincaProducto.getId(), fincaProducto.getFinca().getId(), fincaProducto.getProducto().getId(),
                    determinarTipoMovimientoSegunDestino(destino), -linea.getCantidad(), stockAnterior, stockNuevo,
                    salida.getId(), "salida", "Salida " + salida.getNumero() + " - " + destino, almacenId);
        }
        return List.of(salida.getId());
    }

    private BusinessNotFoundException validationError(String field, String message) {
        return new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND, new ErrorField(field, message)));
    }

    @Override
    public void update(SalidaDto dto, List<ItemSalidaDto> items) {
        Salida salida = repositoryQuery.findById(dto.getId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "No se encontró la salida."))));

        // Obtener cantidad anterior
        List<ItemSalida> itemsAnteriores = itemRepositoryQuery.findBySalidaId(dto.getId());
        if (itemsAnteriores.stream().anyMatch(item -> item.getFincaProductoId() != null)) {
            throw validationError("id", "Los vales de varios productos no se editan. Anúlelo y registre uno nuevo.");
        }
        double cantidadAnterior = itemsAnteriores.stream().mapToDouble(ItemSalida::getCantidad).sum();

        // Calcular nueva cantidad total
        double nuevaCantidadTotal = items.stream().mapToDouble(ItemSalidaDto::getCantidad).sum();

        // Obtener FincaProducto (con producto cargado para obtener precios)
        FincaProducto fincaProducto = fincaProductoReadRepository.findByIdWithDetails(dto.getFincaProductoId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("fincaProductoId", "No se encontró la relación Finca-Producto."))));

        // Forzar inicialización del producto para obtener precios
        Hibernate.initialize(fincaProducto.getProducto());

        // Validar stock (devolver cantidad anterior y restar nueva)
        double stockDisponible = fincaProducto.getStock() + cantidadAnterior;
        if (stockDisponible < nuevaCantidadTotal) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("cantidad", "Stock insuficiente. Stock disponible: " + stockDisponible + ", Cantidad solicitada: " + nuevaCantidadTotal)));
        }

        // Actualizar salida
        salida.setTipo(dto.getTipo());
        salida.setObservaciones(dto.getObservaciones());
        repositoryCommand.save(salida);

        // Obtener el precio según el destino
        Double precio = obtenerPrecioSegunDestino(fincaProducto, dto.getDestino());

        // Revertir deudas de items anteriores si el destino era TRABAJADORES
        if (salida.getDestino() == DestinoSalida.TRABAJADORES) {
            for (ItemSalida itemAnterior : itemsAnteriores) {
                // Solo revertir si no estaba pagado
                if (itemAnterior.getTrabajadorId() != null && (itemAnterior.getPagado() == null || !itemAnterior.getPagado())) {
                    Double valorItem = itemAnterior.getCantidad() * itemAnterior.getPrecio();
                    deudaTrabajadorService.incrementarDeuda(itemAnterior.getTrabajadorId(), -valorItem);
                }
            }
            // Desactivar registros de auditoria anteriores
            deudaDetalleService.desactivarBySalidaId(dto.getId());
        }

        // Eliminar items anteriores y crear nuevos
        itemRepositoryCommand.deleteBySalidaId(dto.getId());
        for (ItemSalidaDto itemDto : items) {
            itemDto.setId(UUID.randomUUID());
            itemDto.setSalidaId(salida.getId());
            itemDto.setPrecio(precio);
            ItemSalida item = new ItemSalida(itemDto);
            itemRepositoryCommand.save(item);

            // Registrar para trabajadores (con o sin deuda según si pagó)
            if (dto.getDestino() == DestinoSalida.TRABAJADORES && itemDto.getTrabajadorId() != null) {
                Double valorItem = itemDto.getCantidad() * precio;
                Boolean yaPago = itemDto.getPagado() != null && itemDto.getPagado();

                // Solo incrementar deuda si NO ha pagado
                if (!yaPago) {
                    deudaTrabajadorService.incrementarDeuda(itemDto.getTrabajadorId(), valorItem);
                }

                // Siempre registrar nuevo detalle de auditoria
                DeudaTrabajadorDetalleDto detalle = DeudaTrabajadorDetalleDto.builder()
                        .id(UUID.randomUUID())
                        .trabajadorId(itemDto.getTrabajadorId())
                        .salidaId(salida.getId())
                        .salidaNumero(salida.getNumero())
                        .salidaTipo(dto.getTipo())
                        .productoId(fincaProducto.getProducto().getId())
                        .productoCodigo(fincaProducto.getProducto().getCode())
                        .productoNombre(fincaProducto.getProducto().getName())
                        .cantidad(itemDto.getCantidad())
                        .precioUnitario(precio)
                        .importe(valorItem)
                        .fecha(LocalDateTime.now())
                        .activo(true)
                        .pagado(yaPago)
                        .tipoMovimiento(TipoMovimiento.COMPRA)
                        .build();
                deudaDetalleService.registrar(detalle);
            }
        }

        // Actualizar stock con auditoría
        Double stockAnterior = fincaProducto.getStock();
        Double stockNuevo = stockDisponible - nuevaCantidadTotal;
        fincaProducto.setStock(stockNuevo);
        fincaProductoWriteRepository.save(fincaProducto);

        // Registrar movimiento de stock solo si hay cambio
        Double diferencia = stockNuevo - stockAnterior;
        if (diferencia != 0) {
            TipoMovimientoStock tipoMov = diferencia > 0
                    ? TipoMovimientoStock.DEVOLUCION
                    : determinarTipoMovimientoSegunDestino(dto.getDestino());
            movimientoStockService.registrarMovimiento(
                    fincaProducto.getId(),
                    fincaProducto.getFinca().getId(),
                    fincaProducto.getProducto().getId(),
                    tipoMov,
                    diferencia,
                    stockAnterior,
                    stockNuevo,
                    salida.getId(),
                    "salida",
                    "Ajuste por edición de Salida " + salida.getNumero()
            );
        }
    }

    @Override
    public void delete(UUID id) {
        Salida salida = repositoryQuery.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "No se encontró la salida."))));

        // Validar que la salida no esté ya anulada
        if (!salida.getActivo()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "La salida ya está anulada.")));
        }

        // Obtener cantidad total para devolver al stock
        List<ItemSalida> items = itemRepositoryQuery.findBySalidaId(id);

        // RN-06: Validar que no haya items pagados
        boolean tieneItemsPagados = items.stream()
                .anyMatch(item -> item.getPagado() != null && item.getPagado());
        if (tieneItemsPagados) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "No se puede eliminar la salida: tiene items pagados.")));
        }

        double cantidadTotal = items.stream().mapToDouble(ItemSalida::getCantidad).sum();

        // Revertir deudas si el destino era TRABAJADORES
        if (salida.getDestino() == DestinoSalida.TRABAJADORES) {
            for (ItemSalida item : items) {
                // Solo revertir si no estaba pagado
                if (item.getTrabajadorId() != null && (item.getPagado() == null || !item.getPagado())) {
                    Double valorItem = item.getCantidad() * item.getPrecio();
                    deudaTrabajadorService.incrementarDeuda(item.getTrabajadorId(), -valorItem);
                }
            }
            // Desactivar registros de auditoria
            deudaDetalleService.desactivarBySalidaId(id);
        }

        // Devolver stock de cada línea. En vales históricos el producto se toma del encabezado.
        for (ItemSalida item : items) {
            UUID fincaProductoId = item.getFincaProductoId() != null ? item.getFincaProductoId() : salida.getFincaProductoId();
            FincaProducto fincaProducto = fincaProductoReadRepository.findByIdWithDetails(fincaProductoId).orElse(null);
            if (fincaProducto == null) continue;
            Double stockAnterior = fincaProducto.getStock();
            Double stockNuevo = stockAnterior + item.getCantidad();
            fincaProducto.setStock(stockNuevo);
            fincaProductoWriteRepository.save(fincaProducto);
            movimientoStockService.registrarMovimiento(
                    fincaProducto.getId(),
                    fincaProducto.getFinca().getId(),
                    fincaProducto.getProducto().getId(),
                    TipoMovimientoStock.DEVOLUCION,
                    item.getCantidad(),
                    stockAnterior,
                    stockNuevo,
                    salida.getId(),
                    "salida",
                    "Devolución por eliminación de Salida " + salida.getNumero()
            );
            if (item.getAlmacenFincaProductoId() != null) {
                almacenFincaProductoReadRepository.findById(item.getAlmacenFincaProductoId()).ifPresent(afp -> {
                    afp.setStock((afp.getStock() == null ? 0 : afp.getStock()) + item.getCantidad());
                    almacenFincaProductoWriteRepository.save(afp);
                });
            }
        }

        // Soft delete: marcar salida como inactiva (los items se mantienen para historial)
        salida.setActivo(false);
        repositoryCommand.save(salida);
    }

    @Override
    public SalidaDto findById(UUID id) {
        Salida salida = repositoryQuery.findByIdWithDetails(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "No se encontró la salida."))));

        SalidaDto dto = salida.toAggregate();
        dto.setItems(salida.getItems().stream()
                .map(ItemSalida::toAggregate)
                .collect(Collectors.toList()));
        return dto;
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        // Construir especificación base con filtros del usuario
        GenericSpecificationsBuilder<Salida> specifications = new GenericSpecificationsBuilder<>(filterCriteria);

        // Agregar filtro de activos por defecto y filtro de tenant
        org.springframework.data.jpa.domain.Specification<Salida> activoSpec = (root, query, cb) -> cb.equal(root.get("activo"), true);
        org.springframework.data.jpa.domain.Specification<Salida> combinedSpec = org.springframework.data.jpa.domain.Specification
                .where(specifications)
                .and(activoSpec)
                .and(TenantSpecification.byFincaViaFincaProducto());

        Page<Salida> data = repositoryQuery.findAll(combinedSpec, pageable);

        List<SalidaResponse> responses = data.getContent().stream()
                .map(s -> new SalidaResponse(
                        s.getId(),
                        s.getTipo(),
                        s.getDestino(),
                        s.getNumero(),
                        s.getFincaProductoId(),
                        s.getFincaProducto() != null && s.getFincaProducto().getFinca() != null ? s.getFincaProducto().getFinca().getCode() : null,
                        s.getFincaProducto() != null && s.getFincaProducto().getFinca() != null ? s.getFincaProducto().getFinca().getName() : null,
                        s.getFincaProducto() != null && s.getFincaProducto().getProducto() != null ? s.getFincaProducto().getProducto().getCode() : null,
                        s.getFincaProducto() != null && s.getFincaProducto().getProducto() != null ? s.getFincaProducto().getProducto().getName() : null,
                        s.getFincaProducto() != null ? s.getFincaProducto().getStock() : null,
                        s.getFecha(),
                        s.getObservaciones(),
                        s.getItems() != null ? s.getItems().stream().mapToDouble(ItemSalida::getCantidad).sum() : 0.0
                ))
                .collect(Collectors.toList());

        return new PaginatedResponse(
                responses,
                data.getTotalPages(),
                data.getNumberOfElements(),
                data.getTotalElements(),
                data.getSize(),
                data.getNumber()
        );
    }

    @Override
    public List<SalidaDto> findValesActivosPorFechaYDestino(LocalDate fecha, DestinoSalida destino) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        UUID fincaEfectiva = TenantContext.shouldFilter() ? TenantContext.getEffectiveFincaId() : null;

        return repositoryQuery.findActivasPorTipoDestinoYFecha(TipoSalida.VALE, destino, inicio, fin)
                .stream()
                .filter(salida -> fincaEfectiva == null
                        || (salida.getFincaProducto() != null
                        && salida.getFincaProducto().getFinca() != null
                        && fincaEfectiva.equals(salida.getFincaProducto().getFinca().getId())))
                .map(salida -> {
                    SalidaDto dto = salida.toAggregate();
                    dto.setItems(salida.getItems().stream()
                            .map(ItemSalida::toAggregate)
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SalidaDto> findValesActivosPorFecha(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        UUID fincaEfectiva = TenantContext.shouldFilter() ? TenantContext.getEffectiveFincaId() : null;

        return repositoryQuery.findActivasPorTipoYFecha(TipoSalida.VALE, inicio, fin)
                .stream()
                .filter(salida -> fincaEfectiva == null
                        || (salida.getFincaProducto() != null
                        && salida.getFincaProducto().getFinca() != null
                        && fincaEfectiva.equals(salida.getFincaProducto().getFinca().getId())))
                .map(salida -> {
                    SalidaDto dto = salida.toAggregate();
                    dto.setItems(salida.getItems().stream()
                            .map(ItemSalida::toAggregate)
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Deprecated
    public String generarNumero(TipoSalida tipo) {
        // Método legacy - usar numeracionService.generarSiguienteNumero() con fincaId
        String prefix = tipo == TipoSalida.VALE ? "VALE-" : "FAC-";
        Integer maxNum = repositoryQuery.findMaxNumeroByPrefix(prefix);
        int nextNum = (maxNum != null ? maxNum : 0) + 1;
        return prefix + String.format("%04d", nextNum);
    }

    private Double obtenerPrecioSegunDestino(FincaProducto fincaProducto, DestinoSalida destino) {
        if (fincaProducto.getProducto() == null) {
            return 0.0;
        }
        return switch (destino) {
            case TRABAJADORES -> fincaProducto.getProducto().getPriceTrabajador();
            case COMEDOR -> fincaProducto.getProducto().getPriceComedor();
            default -> fincaProducto.getProducto().getPrice();
        };
    }

    /**
     * RN-09: Determina el tipo de salida según el destino
     * - VENTA_ESTADO, POBLACION → FACTURA
     * - TRABAJADORES, COMEDOR, INSUMO, OTROS → VALE
     */
    private TipoSalida determinarTipoSegunDestino(DestinoSalida destino) {
        return switch (destino) {
            case VENTA_ESTADO, POBLACION -> TipoSalida.FACTURA;
            default -> TipoSalida.VALE;
        };
    }

    /**
     * Determina el tipo de movimiento de stock según el destino de la salida.
     * Esto permite que el sistema de contabilidad automática aplique el precio correcto.
     */
    private TipoMovimientoStock determinarTipoMovimientoSegunDestino(DestinoSalida destino) {
        return switch (destino) {
            case TRABAJADORES -> TipoMovimientoStock.SALIDA_AUTOCONSUMO;
            case COMEDOR -> TipoMovimientoStock.SALIDA_COMEDOR;
            default -> TipoMovimientoStock.SALIDA_VENTA;
        };
    }
}
