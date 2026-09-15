package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
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

    /**
     * Genera un código único para el reporte con formato: año_mes_consecutivo
     * Ejemplo: 2026_08_01, 2026_08_02, etc.
     */
    String generateCodigo(String year, String mes);

    /**
     * Obtiene todos los reportes donde participó un trabajador en un mes/año específico.
     * Incluye el detalle de días y horas por cada reporte.
     */
    List<ReporteDto> getReportesPorTrabajador(UUID trabajadorId, String year, String mes);
}