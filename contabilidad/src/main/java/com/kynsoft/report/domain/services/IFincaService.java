package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.DeleteFincaResponse;
import com.kynsoft.report.domain.dto.FincaDto;
import com.kynsoft.report.domain.dto.FincaResumenDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IFincaService {

    void create(FincaDto object);

    void update(FincaDto object);

    DeleteFincaResponse delete(UUID id);

    void reactivar(UUID id);

    void asignarResponsable(UUID fincaId, UUID responsableId);

    FincaDto findById(UUID id);

    FincaDto findByCode(String code);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countTrabajadoresByFincaId(UUID fincaId);

    Long countProductosByFincaId(UUID fincaId);

    FincaResumenDto getResumen(UUID fincaId);
}