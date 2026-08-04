package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.TomaPrestamoDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ITomaPrestamoService {

    void create(TomaPrestamoDto object);

    void update(TomaPrestamoDto object);

    void delete(UUID id);

    TomaPrestamoDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
