package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.FincaDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IFincaService {

    void create(FincaDto object);

    void update(FincaDto object);

    void delete(UUID id);

    FincaDto findById(UUID id);

    FincaDto findByCode(String code);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}