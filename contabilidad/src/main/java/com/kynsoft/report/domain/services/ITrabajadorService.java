package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ITrabajadorService {

    void create(TrabajadorDto object);

    void update(TrabajadorDto object);

    void delete(UUID id);

    TrabajadorDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<TrabajadorDto> findAll(List<UUID> ids);
}
