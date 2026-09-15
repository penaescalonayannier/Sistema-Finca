package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.DiaTrabajoDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IDiaTrabajoService {
    
    void create(DiaTrabajoDto object);
    
    void update(DiaTrabajoDto object);
    
    void delete(UUID id);
    
    DiaTrabajoDto findById(UUID id);
    
    List<DiaTrabajoDto> findByReporteId(UUID reporteId);
    
    List<DiaTrabajoDto> findByReporteIdWithTrabajadores(UUID reporteId);
    
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}