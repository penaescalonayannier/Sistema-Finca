package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.TipoAnimalDto;
import com.kynsoft.report.domain.services.ITipoAnimalService;
import com.kynsoft.report.infrastructure.entity.TipoAnimal;
import com.kynsoft.report.infrastructure.repository.command.TipoAnimalWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TipoAnimalReadDataJPARepository;
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
public class TipoAnimalServiceImpl implements ITipoAnimalService {

    private final TipoAnimalWriteDataJPARepository writeRepository;
    private final TipoAnimalReadDataJPARepository readRepository;

    public TipoAnimalServiceImpl(
            TipoAnimalWriteDataJPARepository writeRepository,
            TipoAnimalReadDataJPARepository readRepository) {
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public void create(TipoAnimalDto dto) {
        TipoAnimal entity = new TipoAnimal(dto);
        writeRepository.save(entity);
    }

    @Override
    public void update(TipoAnimalDto dto) {
        TipoAnimal entity = readRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Tipo de Animal not found."))));

        entity.setCodigo(dto.getCodigo());
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setActivo(dto.getActivo());
        entity.setOrden(dto.getOrden());

        writeRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        TipoAnimal entity = readRepository.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Tipo de Animal not found."))));

        writeRepository.delete(entity);
    }

    @Override
    public TipoAnimalDto findById(UUID id) {
        return readRepository.findById(id)
                .map(TipoAnimal::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Tipo de Animal not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<TipoAnimal> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<TipoAnimal> page = readRepository.findAll(specifications, pageable);

        return createPaginatedResponse(page);
    }

    @Override
    public List<TipoAnimalDto> findAllActive() {
        return readRepository.findByActivoTrueOrderByOrdenAsc()
                .stream()
                .map(TipoAnimal::toAggregate)
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

    private PaginatedResponse createPaginatedResponse(Page<TipoAnimal> page) {
        List<TipoAnimalDto> content = page.getContent()
                .stream()
                .map(TipoAnimal::toAggregate)
                .collect(Collectors.toList());

        return new PaginatedResponse(content, page.getTotalPages(), page.getNumberOfElements(),
                page.getTotalElements(), page.getSize(), page.getNumber());
    }
}
