package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.GrupoActivoFijoDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestión de Grupos de Activos Fijos Tangibles.
 *
 * Referencia: NCC No. 7 (Resolución 1038/2017 MFP)
 */
public interface IGrupoActivoFijoService {

    GrupoActivoFijoDto create(GrupoActivoFijoDto dto);

    GrupoActivoFijoDto update(GrupoActivoFijoDto dto);

    void delete(UUID id);

    Optional<GrupoActivoFijoDto> findById(UUID id);

    Optional<GrupoActivoFijoDto> findByCodigo(String codigo);

    List<GrupoActivoFijoDto> findAll();

    List<GrupoActivoFijoDto> findAllActivos();
}
