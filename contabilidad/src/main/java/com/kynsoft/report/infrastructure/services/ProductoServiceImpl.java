package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.ProductoResponse;
import com.kynsoft.report.domain.dto.ProductoDto;
import com.kynsoft.report.domain.services.IProductoService;
import com.kynsoft.report.infrastructure.entity.Producto;
import com.kynsoft.report.infrastructure.repository.command.ProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ProductoReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoServiceImpl implements IProductoService {

    private final ProductoWriteDataJPARepository repositoryCommand;
    private final ProductoReadDataJPARepository repositoryQuery;

    public ProductoServiceImpl(ProductoWriteDataJPARepository repositoryCommand,
                               ProductoReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public void create(ProductoDto object) {
        // Validar que el código no exista
        if (object.getCode() != null) {
            repositoryQuery.findByCode(object.getCode())
                .ifPresent(producto -> {
                    throw new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("code", "Product with code " + object.getCode() + " already exists.")));
                });
        }
        repositoryCommand.save(new Producto(object));
    }

    @Override
    public void update(ProductoDto object) {
        // Verificar que el producto exista
        repositoryQuery.findById(object.getId())
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Product not found."))));
        
        // Verificar que el código no esté duplicado (excluyendo el mismo producto)
        if (object.getCode() != null) {
            repositoryQuery.findByCode(object.getCode())
                .ifPresent(producto -> {
                    if (!producto.getId().equals(object.getId())) {
                        throw new BusinessNotFoundException(new GlobalBusinessException(
                                DomainErrorMessage.BUSINESS_NOT_FOUND,
                                new ErrorField("code", "Product with code " + object.getCode() + " already exists.")));
                    }
                });
        }
        
        repositoryCommand.save(new Producto(object));
    }

    @Override
    public void delete(UUID id) {
        Producto producto = repositoryQuery.findById(id)
            .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Product not found."))));

        // Soft delete: marcar como inactivo
        producto.setActive(false);
        repositoryCommand.save(producto);
    }

    @Override
    public ProductoDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Producto::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Product not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        // Construir especificación base con filtros del usuario
        GenericSpecificationsBuilder<Producto> specifications = new GenericSpecificationsBuilder<>(filterCriteria);

        // Agregar filtro de activos por defecto
        org.springframework.data.jpa.domain.Specification<Producto> activoSpec = (root, query, cb) -> cb.equal(root.get("active"), true);
        org.springframework.data.jpa.domain.Specification<Producto> combinedSpec = org.springframework.data.jpa.domain.Specification.where(specifications).and(activoSpec);

        Page<Producto> data = repositoryQuery.findAll(combinedSpec, pageable);
        return createPaginatedResponse(data);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria, String query) {
        Page<Producto> data;

        // Especificación de activos
        org.springframework.data.jpa.domain.Specification<Producto> activoSpec = (root, q, cb) -> cb.equal(root.get("active"), true);

        if (query != null && !query.trim().isEmpty()) {
            // Si hay texto de búsqueda, usar búsqueda por texto (ya filtra activos en el query)
            data = repositoryQuery.searchByText(query.trim(), pageable);
        } else if (filterCriteria != null && !filterCriteria.isEmpty()) {
            // Si hay filtros, agregar filtro de activos
            GenericSpecificationsBuilder<Producto> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
            org.springframework.data.jpa.domain.Specification<Producto> combinedSpec = org.springframework.data.jpa.domain.Specification.where(specifications).and(activoSpec);
            data = repositoryQuery.findAll(combinedSpec, pageable);
        } else {
            // Sin filtros ni texto, filtrar solo activos
            data = repositoryQuery.findAll(activoSpec, pageable);
        }

        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Producto> data) {
        List<ProductoResponse> responses = data.getContent().stream()
                .map(Producto::toAggregate)
                .map(ProductoResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
    
    // Métodos adicionales útiles
    public ProductoDto findByCode(String code) {
        return repositoryQuery.findByCode(code)
                .map(Producto::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("code", "Product with code " + code + " not found."))));
    }
    
    public List<ProductoDto> findActiveProducts() {
        return repositoryQuery.findByActiveTrue().stream()
                .map(Producto::toAggregate)
                .collect(Collectors.toList());
    }
    
    public List<ProductoDto> findLowStockProducts(Integer threshold) {
        return repositoryQuery.findByStockLessThan(threshold).stream()
                .map(Producto::toAggregate)
                .collect(Collectors.toList());
    }
}