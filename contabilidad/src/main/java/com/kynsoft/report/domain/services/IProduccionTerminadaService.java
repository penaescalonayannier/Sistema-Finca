package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.CreateProduccionTerminadaResult;
import com.kynsoft.report.domain.dto.DeleteProduccionTerminadaResult;
import com.kynsoft.report.domain.dto.ProduccionTerminadaDto;
import com.kynsoft.report.domain.dto.UpdateProduccionTerminadaResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IProduccionTerminadaService {

    /**
     * Crea una producción terminada.
     * - Valida cantidad > 0 (RN-03)
     * - Valida producto asignado a finca (RN-01)
     * - Valida trabajadores de misma finca (RN-04)
     * - Valida trabajadores diferentes (RN-05)
     * - Incrementa stock automáticamente (RN-02)
     */
    CreateProduccionTerminadaResult create(ProduccionTerminadaDto dto);

    /**
     * Actualiza una producción terminada.
     * - Ajusta stock según diferencia de cantidad (RN-06)
     * - Valida trabajadores de misma finca (RN-04)
     * - Valida trabajadores diferentes (RN-05)
     * - Valida que no quede stock negativo
     */
    UpdateProduccionTerminadaResult update(ProduccionTerminadaDto dto);

    /**
     * Elimina (soft delete) una producción terminada.
     * - Revierte el stock (RN-07)
     * - Valida que no quede stock negativo
     */
    DeleteProduccionTerminadaResult delete(UUID id);

    ProduccionTerminadaDto findById(UUID id);

    List<ProduccionTerminadaDto> findByProductoId(UUID productoId);

    List<ProduccionTerminadaDto> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<ProduccionTerminadaDto> findByTrabajadorEntregaId(UUID trabajadorId);

    List<ProduccionTerminadaDto> findByTrabajadorRecibeId(UUID trabajadorId);

    List<ProduccionTerminadaDto> findByFincaId(UUID fincaId);

    List<ProduccionTerminadaDto> findByFincaIdAndFechaBetween(UUID fincaId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
