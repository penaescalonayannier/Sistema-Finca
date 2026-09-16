package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.AlmacenResponse;
import com.kynsoft.report.domain.dto.AlmacenDto;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.services.IAlmacenService;
import com.kynsoft.report.infrastructure.entity.Almacen;
import com.kynsoft.report.infrastructure.entity.AlmacenFincaProducto;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.FincaProducto;
import com.kynsoft.report.infrastructure.repository.command.AlmacenFincaProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.AlmacenWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AlmacenFincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AlmacenReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
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
public class AlmacenServiceImpl implements IAlmacenService {

    private final AlmacenWriteDataJPARepository repositoryCommand;
    private final AlmacenReadDataJPARepository repositoryQuery;
    private final FincaProductoReadDataJPARepository fincaProductoRepository;
    private final FincaReadDataJPARepository fincaRepository;
    private final AlmacenFincaProductoWriteDataJPARepository afpWriteRepository;
    private final AlmacenFincaProductoReadDataJPARepository afpReadRepository;

    public AlmacenServiceImpl(AlmacenWriteDataJPARepository repositoryCommand,
                              AlmacenReadDataJPARepository repositoryQuery,
                              FincaProductoReadDataJPARepository fincaProductoRepository,
                              FincaReadDataJPARepository fincaRepository,
                              AlmacenFincaProductoWriteDataJPARepository afpWriteRepository,
                              AlmacenFincaProductoReadDataJPARepository afpReadRepository) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.fincaProductoRepository = fincaProductoRepository;
        this.fincaRepository = fincaRepository;
        this.afpWriteRepository = afpWriteRepository;
        this.afpReadRepository = afpReadRepository;
    }

    @Override
    public UUID create(AlmacenDto object) {
        // Validar que la finca exista
        if (object.getFincaId() == null) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("fincaId", "Finca es requerida.")));
        }

        Finca finca = fincaRepository.findById(object.getFincaId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("fincaId", "Finca no encontrada."))));

        // RN-02: Validar nombre único por finca
        if (existsByNombreAndFincaId(object.getNombre(), object.getFincaId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("nombre", "Nombre ya existe en esta finca.")));
        }

        // RN-01: Generar código de inventario automáticamente
        String inventario = generateInventarioCode(object.getFincaId());

        Almacen almacen = new Almacen();
        almacen.setId(UUID.randomUUID());
        almacen.setNombre(object.getNombre());
        almacen.setDescripcion(object.getDescripcion());
        almacen.setInventario(inventario);
        almacen.setEsPrincipal(false);
        almacen.setActivo(true);
        almacen.setFinca(finca);

        repositoryCommand.save(almacen);
        return almacen.getId();
    }

    @Override
    public void update(AlmacenDto object) {
        log.info("Actualizando almacén: id={}, nombre={}", object.getId(), object.getNombre());

        Almacen existing = repositoryQuery.findById(object.getId())
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Almacen no encontrado."))));

        log.info("Almacén existente: nombre={}", existing.getNombre());

        UUID fincaId = existing.getFinca() != null ? existing.getFinca().getId() : null;

        // RN-02: Validar nombre único por finca (solo si cambió)
        if (object.getNombre() != null && !object.getNombre().equals(existing.getNombre())) {
            if (existsByNombreAndFincaIdAndIdNot(object.getNombre(), fincaId, object.getId())) {
                throw new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("nombre", "Nombre ya existe en esta finca.")));
            }
            existing.setNombre(object.getNombre());
            log.info("Nombre actualizado a: {}", object.getNombre());
        }

        // Solo actualizar descripcion si se envía (no sobreescribir con null)
        if (object.getDescripcion() != null) {
            existing.setDescripcion(object.getDescripcion());
        }
        // Nota: inventario y fincaId no se pueden modificar

        repositoryCommand.save(existing);
        log.info("Almacén guardado exitosamente");
    }

    @Override
    public void delete(UUID id) {
        Almacen almacen = repositoryQuery.findByIdWithProductos(id)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Almacen no encontrado."))));

        // Validar que no esté ya inactivo
        if (!almacen.getActivo()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Almacen ya está inactivo.")));
        }

        // RN-10: No eliminar almacén principal
        if (Boolean.TRUE.equals(almacen.getEsPrincipal())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "No se puede eliminar el almacén principal. Designe otro almacén como principal primero.")));
        }

        // RN-09: No eliminar con stock > 0
        double stockTotal = almacen.getProductos().stream()
                .mapToDouble(fp -> fp.getStock() != null ? fp.getStock() : 0.0)
                .sum();
        if (stockTotal > 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Almacén tiene productos con stock. Transfiera a otro almacén primero. Stock total: " + stockTotal)));
        }

        // Soft delete
        almacen.setActivo(false);
        repositoryCommand.save(almacen);
    }

    @Override
    public void reactivar(UUID id) {
        Almacen almacen = repositoryQuery.findById(id)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Almacen no encontrado."))));

        if (almacen.getActivo()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Almacen ya está activo.")));
        }

        almacen.setActivo(true);
        repositoryCommand.save(almacen);
    }

    @Override
    public void establecerPrincipal(UUID id) {
        Almacen almacen = repositoryQuery.findById(id)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Almacen no encontrado."))));

        if (!almacen.getActivo()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Almacen inactivo no puede ser establecido como principal.")));
        }

        if (Boolean.TRUE.equals(almacen.getEsPrincipal())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Almacen ya es principal.")));
        }

        // Quitar el flag de principal del almacén actual
        UUID fincaId = almacen.getFinca().getId();
        repositoryQuery.findByFincaIdAndEsPrincipalTrue(fincaId)
                .ifPresent(principalAnterior -> {
                    principalAnterior.setEsPrincipal(false);
                    repositoryCommand.save(principalAnterior);
                });

        // Establecer este almacén como principal
        almacen.setEsPrincipal(true);
        repositoryCommand.save(almacen);
    }

    @Override
    public AlmacenDto findById(UUID id) {
        // Usar EntityGraph para cargar productos con sus relaciones
        Almacen almacen = repositoryQuery.findByIdWithProductos(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Almacen no encontrado."))));

        AlmacenDto dto = almacen.toAggregate();

        // Cargar productos con información completa desde almacenProductos
        if (almacen.getAlmacenProductos() != null) {
            List<FincaProductoDto> productosDto = almacen.getAlmacenProductos().stream()
                .filter(afp -> afp.getActivo() != null && afp.getActivo())
                .map(afp -> {
                    FincaProducto fp = afp.getFincaProducto();
                    return FincaProductoDto.builder()
                        .id(afp.getId()) // Usar el ID de AlmacenFincaProducto para operaciones
                        .fincaProductoId(fp != null ? fp.getId() : null) // ID real del FincaProducto para salidas
                        .fincaId(fp != null && fp.getFinca() != null ? fp.getFinca().getId() : null)
                        .fincaCode(fp != null && fp.getFinca() != null ? fp.getFinca().getCode() : null)
                        .fincaName(fp != null && fp.getFinca() != null ? fp.getFinca().getName() : null)
                        .productoId(fp != null && fp.getProducto() != null ? fp.getProducto().getId() : null)
                        .productoCode(fp != null && fp.getProducto() != null ? fp.getProducto().getCode() : null)
                        .productoName(fp != null && fp.getProducto() != null ? fp.getProducto().getName() : null)
                        .productoPrice(fp != null && fp.getProducto() != null ? fp.getProducto().getPrice() : null)
                        .stock(afp.getStock()) // Stock del almacén
                        .stockMinimo(afp.getStockMinimo())
                        .stockMaximo(afp.getStockMaximo())
                        .activo(afp.getActivo())
                        .build();
                })
                .collect(Collectors.toList());
            dto.setProductos(productosDto);
        }

        return dto;
    }

    @Override
    public AlmacenDto findByInventario(String inventario) {
        return repositoryQuery.findByInventario(inventario)
                .map(Almacen::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("inventario", "Almacen con inventario " + inventario + " no encontrado."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Almacen> specifications = new GenericSpecificationsBuilder<>(filterCriteria);

        // Filtrar solo activos
        Specification<Almacen> activoSpec = (root, query, cb) -> cb.equal(root.get("activo"), true);
        Specification<Almacen> combinedSpec = Specification.where(specifications).and(activoSpec);

        Page<Almacen> data = repositoryQuery.findAll(combinedSpec, pageable);
        return createPaginatedResponse(data);
    }

    @Override
    public PaginatedResponse findByFincaId(UUID fincaId, Pageable pageable) {
        Page<Almacen> data = repositoryQuery.findByFincaIdAndActivoTrue(fincaId, pageable);
        return createPaginatedResponse(data);
    }

    @Override
    public String generateInventarioCode(UUID fincaId) {
        // Obtener el último número de inventario para esta finca
        Long count = repositoryQuery.countByFincaId(fincaId);
        return String.format("INV-%06d", count + 1);
    }

    @Override
    public boolean existsByNombreAndFincaId(String nombre, UUID fincaId) {
        return repositoryQuery.existsByNombreIgnoreCaseAndFincaIdAndActivoTrue(nombre, fincaId);
    }

    @Override
    public boolean existsByNombreAndFincaIdAndIdNot(String nombre, UUID fincaId, UUID id) {
        return repositoryQuery.existsByNombreIgnoreCaseAndFincaIdAndIdNotAndActivoTrue(nombre, fincaId, id);
    }

    @Override
    public void addProducto(UUID almacenId, UUID fincaProductoId) {
        Almacen almacen = repositoryQuery.findById(almacenId)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("almacenId", "Almacen no encontrado."))));

        FincaProducto fincaProducto = fincaProductoRepository.findById(fincaProductoId)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("fincaProductoId", "Producto no encontrado."))));

        if (!Boolean.TRUE.equals(almacen.getActivo())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("almacenId", "No se pueden agregar productos a un almacén inactivo.")));
        }
        if (!Boolean.TRUE.equals(fincaProducto.getActivo())
                || !almacen.getFinca().getId().equals(fincaProducto.getFinca().getId())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("fincaProductoId", "El producto debe estar activo y pertenecer a la misma finca del almacén.")));
        }

        // Verificar si ya existe la relación
        if (afpReadRepository.existsByAlmacenIdAndFincaProductoIdAndActivoTrue(almacenId, fincaProductoId)) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("fincaProductoId", "Producto ya existe en este almacén.")));
        }

        // Crear nueva relación AlmacenFincaProducto
        AlmacenFincaProducto afp = new AlmacenFincaProducto();
        afp.setId(UUID.randomUUID());
        afp.setAlmacen(almacen);
        afp.setFincaProducto(fincaProducto);
        afp.setStock(0.0);
        afp.setStockMinimo(0.0);
        afp.setActivo(true);
        afpWriteRepository.save(afp);
    }

    @Override
    public void removeProducto(UUID almacenId, UUID fincaProductoId) {
        // fincaProductoId aquí es en realidad el ID de AlmacenFincaProducto
        AlmacenFincaProducto afp = afpReadRepository.findById(fincaProductoId)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("fincaProductoId", "Producto no encontrado en almacén."))));

        // Validar que pertenece al almacén correcto
        if (!afp.getAlmacen().getId().equals(almacenId)) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("almacenId", "Producto no pertenece a este almacén.")));
        }

        if (!Boolean.TRUE.equals(afp.getActivo())) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("fincaProductoId", "El producto ya está inactivo en el almacén.")));
        }
        if (afp.getStock() != null && afp.getStock() > 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("fincaProductoId", "No se puede remover un producto con existencias. Transfiéralo o ajústelo mediante un movimiento documentado.")));
        }

        // Soft delete
        afp.setActivo(false);
        afpWriteRepository.save(afp);
    }

    private PaginatedResponse createPaginatedResponse(Page<Almacen> data) {
        List<AlmacenResponse> responses = data.getContent().stream()
                .map(almacen -> {
                    AlmacenDto dto = almacen.toAggregate();
                    // Usar query para contar productos (evita LazyInitializationException)
                    int productCount = repositoryQuery.countProductosByAlmacenId(almacen.getId());
                    return new AlmacenResponse(dto, productCount);
                })
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
