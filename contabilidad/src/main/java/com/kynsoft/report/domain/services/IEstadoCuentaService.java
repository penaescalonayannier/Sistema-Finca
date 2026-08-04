package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.EstadoCuentaDto;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IEstadoCuentaService {

    void create(EstadoCuentaDto object);

    void update(EstadoCuentaDto object);

    void delete(UUID id);

    void deleteIds(List<UUID> ids);

    EstadoCuentaDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<EstadoCuentaDto> findAll(String query, String fechaInicio, String fechaFin, String filterTipo);

    List<EstadoCuentaDto> findAllByDate(LocalDate fechaInicio, LocalDate fechaFin);
}
