package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.CampoDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ICamposService {

    void create(CampoDto object);

    void update(CampoDto object);

    void delete(UUID id);

    CampoDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
