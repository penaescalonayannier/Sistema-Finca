package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.PrestamoDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IPrestamoService {

    void create(PrestamoDto object);

    void update(PrestamoDto object);

    void delete(UUID id);

    PrestamoDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
