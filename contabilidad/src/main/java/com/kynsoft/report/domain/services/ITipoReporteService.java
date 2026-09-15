package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.TipoReporteDto;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ITipoReporteService {

    void create(TipoReporteDto dto);

    void update(TipoReporteDto dto);

    void delete(UUID id);

    TipoReporteDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<TipoReporteDto> findAllActive();

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, UUID id);
}
