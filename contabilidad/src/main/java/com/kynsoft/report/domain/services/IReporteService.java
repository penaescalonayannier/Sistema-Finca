package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.ReporteConsolidadoDto;
import com.kynsoft.report.domain.dto.ReporteConsolidadoPorResponsablePdfDto;
import com.kynsoft.report.domain.dto.ReporteDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IReporteService {

    void create(ReporteDto object);

    void update(ReporteDto object);

    void delete(UUID id);

    ReporteDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    ReporteConsolidadoDto getConsolidado(String year, String mes);

    ReporteConsolidadoPorResponsablePdfDto getConsolidadoPorResponsable(String year, String mes);
}