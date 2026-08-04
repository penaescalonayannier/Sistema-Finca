package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.TrabajadorReporteDetailDto;
import com.kynsoft.report.domain.dto.TrabajadorReporteDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ITrabajadorReporteService {
    
    void asignarTrabajadorAReporte(TrabajadorReporteDto object);
    
    void actualizarTrabajadorEnReporte(TrabajadorReporteDto object);
    
    TrabajadorReporteDto findById(UUID id);
    
    void remover(UUID id);
    
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
    
    // Método para obtener trabajadores con detalles completos
    List<TrabajadorReporteDetailDto> obtenerTrabajadoresConDetallesPorReporte(UUID reporteId);
}