package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.TipoAnimalDto;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ITipoAnimalService {

    void create(TipoAnimalDto dto);

    void update(TipoAnimalDto dto);

    void delete(UUID id);

    TipoAnimalDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<TipoAnimalDto> findAllActive();

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, UUID id);
}
