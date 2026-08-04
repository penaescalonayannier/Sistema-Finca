package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.ClienteDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IClienteService {

    void create(ClienteDto object);

    void update(ClienteDto object);

    void delete(UUID id);

    ClienteDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
