package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.CategoriaTipoCultivo;
import com.kynsoft.report.domain.dto.TipoCultivoDto;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ITipoCultivoService {

    void create(TipoCultivoDto dto);

    void update(TipoCultivoDto dto);

    void delete(UUID id);

    TipoCultivoDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<TipoCultivoDto> findAllActive();

    /**
     * Encuentra todos los tipos de cultivo activos filtrados por categoría
     */
    List<TipoCultivoDto> findActiveByCategoria(CategoriaTipoCultivo categoria);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, UUID id);
}
