package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.EvaluacionDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IEvaluacionService {

    void create(EvaluacionDto object);

    void update(EvaluacionDto object);

    void delete(UUID id);

    EvaluacionDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
