package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.TipoReporteDto;
import com.kynsoft.report.domain.services.ITipoReporteService;
import com.kynsoft.report.infrastructure.entity.TipoReporte;
import com.kynsoft.report.infrastructure.repository.command.TipoReporteWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TipoReporteReadDataJPARepository;
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
public class TipoReporteServiceImpl implements ITipoReporteService {

    private final TipoReporteWriteDataJPARepository writeRepository;
    private final TipoReporteReadDataJPARepository readRepository;

    public TipoReporteServiceImpl(
            TipoReporteWriteDataJPARepository writeRepository,
            TipoReporteReadDataJPARepository readRepository) {
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public void create(TipoReporteDto dto) {
        TipoReporte entity = new TipoReporte(dto);
        writeRepository.save(entity);
    }

    @Override
    public void update(TipoReporteDto dto) {
        TipoReporte entity = readRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Tipo de Reporte not found."))));

        entity.setCodigo(dto.getCodigo());
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setCodigoCentroCosto(dto.getCodigoCentroCosto());
        entity.setTipoSubclasificacion(dto.getTipoSubclasificacion());
        entity.setTipoCultivoCategoriaFiltro(dto.getTipoCultivoCategoriaFiltro());
        entity.setTipoCultivoAutoId(dto.getTipoCultivoAutoId());
        entity.setRequiereCampo(dto.getRequiereCampo());
        entity.setActivo(dto.getActivo());
        entity.setOrden(dto.getOrden());

        writeRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        TipoReporte entity = readRepository.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Tipo de Reporte not found."))));

        writeRepository.delete(entity);
    }

    @Override
    public TipoReporteDto findById(UUID id) {
        return readRepository.findById(id)
                .map(TipoReporte::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Tipo de Reporte not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<TipoReporte> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<TipoReporte> page = readRepository.findAll(specifications, pageable);

        return createPaginatedResponse(page);
    }

    @Override
    public List<TipoReporteDto> findAllActive() {
        return readRepository.findByActivoTrueOrderByOrdenAsc()
                .stream()
                .map(TipoReporte::toAggregate)
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

    private PaginatedResponse createPaginatedResponse(Page<TipoReporte> page) {
        List<TipoReporteDto> content = page.getContent()
                .stream()
                .map(TipoReporte::toAggregate)
                .collect(Collectors.toList());

        return new PaginatedResponse(content, page.getTotalPages(), page.getNumberOfElements(),
                page.getTotalElements(), page.getSize(), page.getNumber());
    }
}
