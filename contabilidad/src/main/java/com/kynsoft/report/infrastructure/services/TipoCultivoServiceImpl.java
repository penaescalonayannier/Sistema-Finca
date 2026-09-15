package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.CategoriaTipoCultivo;
import com.kynsoft.report.domain.dto.TipoCultivoDto;
import com.kynsoft.report.domain.services.ITipoCultivoService;
import com.kynsoft.report.infrastructure.entity.TipoCultivo;
import com.kynsoft.report.infrastructure.repository.command.TipoCultivoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TipoCultivoReadDataJPARepository;
import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TipoCultivoServiceImpl implements ITipoCultivoService {

    private final TipoCultivoWriteDataJPARepository writeRepository;
    private final TipoCultivoReadDataJPARepository readRepository;

    public TipoCultivoServiceImpl(
            TipoCultivoWriteDataJPARepository writeRepository,
            TipoCultivoReadDataJPARepository readRepository) {
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public void create(TipoCultivoDto dto) {
        TipoCultivo entity = new TipoCultivo(dto);
        writeRepository.save(entity);
    }

    @Override
    public void update(TipoCultivoDto dto) {
        TipoCultivo entity = readRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Tipo de Cultivo not found."))));

        entity.setCodigo(dto.getCodigo());
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setCategoria(dto.getCategoria());
        entity.setRequiereCampo(dto.getRequiereCampo());
        entity.setActivo(dto.getActivo());
        entity.setOrden(dto.getOrden());

        writeRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        TipoCultivo entity = readRepository.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Tipo de Cultivo not found."))));

        writeRepository.delete(entity);
    }

    @Override
    public TipoCultivoDto findById(UUID id) {
        return readRepository.findById(id)
                .map(TipoCultivo::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Tipo de Cultivo not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<TipoCultivo> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<TipoCultivo> page = readRepository.findAll(specifications, pageable);

        return createPaginatedResponse(page);
    }

    @Override
    public List<TipoCultivoDto> findAllActive() {
        return readRepository.findByActivoTrueOrderByOrdenAsc()
                .stream()
                .map(TipoCultivo::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<TipoCultivoDto> findActiveByCategoria(CategoriaTipoCultivo categoria) {
        return readRepository.findByCategoriaAndActivoTrueOrderByOrdenAsc(categoria)
                .stream()
                .map(TipoCultivo::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return readRepository.existsByCodigo(codigo);
    }

    @Override
    public boolean existsByCodigoAndIdNot(String codigo, UUID id) {
        return readRepository.existsByCodigoAndIdNot(codigo, id);
    }

    private PaginatedResponse createPaginatedResponse(Page<TipoCultivo> page) {
        List<TipoCultivoDto> content = page.getContent()
                .stream()
                .map(TipoCultivo::toAggregate)
                .collect(Collectors.toList());

        return new PaginatedResponse(content, page.getTotalPages(), page.getNumberOfElements(),
                page.getTotalElements(), page.getSize(), page.getNumber());
    }
}
