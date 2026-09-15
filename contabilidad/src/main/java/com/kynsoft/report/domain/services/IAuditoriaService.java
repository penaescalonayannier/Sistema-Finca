package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.AuditoriaDto;
import com.kynsoft.report.domain.dto.TipoAccion;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IAuditoriaService {

    /**
     * Register an audit event
     */
    void registrar(TipoAccion accion, String entidad, UUID entidadId,
                   String descripcion, Object valorAnterior, Object valorNuevo);

    /**
     * Register an audit event with explicit user info (for login/logout)
     */
    void registrar(UUID usuarioId, String username, TipoAccion accion,
                   String entidad, UUID entidadId, String descripcion,
                   Object valorAnterior, Object valorNuevo, String ipAddress);

    /**
     * Find audit record by ID
     */
    AuditoriaDto findById(UUID id);

    /**
     * Search audit records with filters
     */
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    /**
     * Get audit history for a specific entity
     */
    List<AuditoriaDto> getHistorialEntidad(String entidad, UUID entidadId);

    /**
     * Get distinct entity names for filter dropdown
     */
    List<String> getEntidades();
}
