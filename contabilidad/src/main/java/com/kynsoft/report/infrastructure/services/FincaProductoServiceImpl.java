package com.kynsoft.report.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.FincaProductoResponse;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.services.IFincaProductoService;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.FincaProducto;
import com.kynsoft.report.infrastructure.entity.Producto;
import com.kynsoft.report.infrastructure.repository.command.FincaProductoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ProductoReadDataJPARepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional
public class FincaProductoServiceImpl implements IFincaProductoService {

    private final FincaProductoWriteDataJPARepository repositoryCommand;
    private final FincaProductoReadDataJPARepository repositoryQuery;
    private final FincaReadDataJPARepository fincaRepository;
    private final ProductoReadDataJPARepository productoRepository;

    public FincaProductoServiceImpl(
            FincaProductoWriteDataJPARepository repositoryCommand,
            FincaProductoReadDataJPARepository repositoryQuery,
            FincaReadDataJPARepository fincaRepository,
            ProductoReadDataJPARepository productoRepository) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.fincaRepository = fincaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public void asignarProductoAFinca(UUID fincaId, UUID productoId, Integer stock) {
        // Validar que la finca exista
        Finca finca = fincaRepository.findById(fincaId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("fincaId", "Finca not found."))));

        // Validar que el producto exista
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("productoId", "Producto not found."))));

        // Validar que no exista ya la relación
        repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId)
                .ifPresent(fp -> {
                    throw new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("fincaId", "Product already assigned to this finca.")));
                });

        // Crear la relación
        FincaProducto fincaProducto = new FincaProducto();
        fincaProducto.setId(UUID.randomUUID());
        fincaProducto.setFinca(finca);
        fincaProducto.setProducto(producto);
        fincaProducto.setStock(stock);

        repositoryCommand.save(fincaProducto);
    }

    @Override
    public void actualizarStock(UUID fincaId, UUID productoId, Integer stock) {
        FincaProducto fincaProducto = repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("fincaId", "Relationship not found."))));

        fincaProducto.setStock(stock);
        repositoryCommand.save(fincaProducto);
    }

    @Override
    public void removerProductoDeFinca(UUID fincaId, UUID productoId) {
        FincaProducto fincaProducto = repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("fincaId", "Relationship not found."))));

        repositoryCommand.delete(fincaProducto);
    }

    @Override
    public void removerTodosProductosDeFinca(UUID fincaId) {
        // Verificar que la finca exista
        fincaRepository.findById(fincaId)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("fincaId", "Finca not found."))));

        repositoryCommand.deleteByFincaId(fincaId);
    }

    @Override
    public List<FincaProductoDto> obtenerProductosDeFinca(UUID fincaId) {
        return repositoryQuery.findWithDetailsByFincaId(fincaId)
                .stream()
                .map(this::toDtoWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<FincaProductoDto> obtenerFincasDeProducto(UUID productoId) {
        return repositoryQuery.findWithDetailsByProductoId(productoId)
                .stream()
                .map(this::toDtoWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public FincaProductoDto obtenerRelacion(UUID fincaId, UUID productoId) {
        return repositoryQuery.findWithDetailsByFincaIdAndProductoId(fincaId, productoId)
                .map(this::toDtoWithDetails)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("fincaId", "Relationship not found."))));
    }

    @Override
    public Integer obtenerStock(UUID fincaId, UUID productoId) {
        return repositoryQuery.findByFincaIdAndProductoId(fincaId, productoId)
                .map(FincaProducto::getStock)
                .orElse(0);
    }

    private FincaProductoDto toDtoWithDetails(FincaProducto fp) {
        return FincaProductoDto.builder()
                .id(fp.getId())
                .fincaId(fp.getFinca().getId())
                .fincaCode(fp.getFinca().getCode())
                .fincaName(fp.getFinca().getName())
                .productoId(fp.getProducto().getId())
                .productoCode(fp.getProducto().getCode())
                .productoName(fp.getProducto().getName())
                .productoPrice(fp.getProducto().getPrice())
                .stock(fp.getStock())
                .build();
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<FincaProducto> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<FincaProducto> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<FincaProducto> data) {
        List<FincaProductoResponse> responses = data.getContent().stream()
                .map(fp -> new FincaProductoResponse(
                fp.getId(),
                fp.getFinca().getId(),
                fp.getFinca().getCode(),
                fp.getFinca().getName(),
                fp.getProducto().getId(),
                fp.getProducto().getCode(),
                fp.getProducto().getName(),
                fp.getProducto().getPrice(),
                fp.getStock()
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

}
