package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.LaborDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ILaborService {
    void create(LaborDto object);
    void update(LaborDto object);
    void delete(UUID id);
    LaborDto findById(UUID id);
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}