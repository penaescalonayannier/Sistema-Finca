package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.InstrumentoTrabajoDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IInstrumentoTrabajoService {

    void create(InstrumentoTrabajoDto object);

    void update(InstrumentoTrabajoDto object);

    void delete(UUID id);

    InstrumentoTrabajoDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
