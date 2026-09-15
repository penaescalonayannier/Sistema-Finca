package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.TrabajadorDiaDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ITrabajadorDiaService {
    
    void create(TrabajadorDiaDto object);
    
    void update(TrabajadorDiaDto object);
    
    void delete(UUID id);
    
    TrabajadorDiaDto findById(UUID id);
    
    List<TrabajadorDiaDto> findByDiaTrabajoId(UUID diaTrabajoId);
    
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}