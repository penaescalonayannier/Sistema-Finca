package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.HombreActividadAgricolaImporteDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IHombreActividadAgricolaImporteService {
    void create(HombreActividadAgricolaImporteDto object);
    void update(HombreActividadAgricolaImporteDto object);
    void delete(UUID id);
    HombreActividadAgricolaImporteDto findById(UUID id);
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}