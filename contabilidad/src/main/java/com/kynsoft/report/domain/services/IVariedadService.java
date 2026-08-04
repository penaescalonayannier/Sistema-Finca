package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.VariedadDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IVariedadService {

    void create(VariedadDto object);

    void update(VariedadDto object);

    void delete(UUID id);

    VariedadDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
