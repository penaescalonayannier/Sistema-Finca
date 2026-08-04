package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.UnidadMedidaDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IUnidadMedidaService {

    void create(UnidadMedidaDto object);

    void update(UnidadMedidaDto object);

    void delete(UUID id);

    UnidadMedidaDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
