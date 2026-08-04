package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.GrupoDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IGrupoService {

    void create(GrupoDto object);

    void update(GrupoDto object);

    void delete(UUID id);

    GrupoDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
