package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.PlanIngresoVentaDto;
import com.kynsoft.report.domain.dto.EstadoPlanIngresoVenta;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.data.domain.Pageable;

public interface IPlanIngresoVentaService {
    void create(PlanIngresoVentaDto plan);
    void update(PlanIngresoVentaDto plan);
    void delete(UUID id);
    PlanIngresoVentaDto findById(UUID id);
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
    PlanIngresoVentaDto generarSiguiente(LocalDate periodo);
    PlanIngresoVentaDto cambiarEstado(UUID id, EstadoPlanIngresoVenta estado);
    Map<String, Object> ejecucion(UUID id);
}
