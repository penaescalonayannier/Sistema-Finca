package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.ActivoFijoTangibleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestión de Activos Fijos Tangibles.
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP)
 * - Resolución 60/2011 CGR: Control interno
 */
public interface IActivoFijoTangibleService {

    ActivoFijoTangibleDto create(ActivoFijoTangibleDto dto);

    ActivoFijoTangibleDto update(ActivoFijoTangibleDto dto);

    void delete(UUID id);

    Optional<ActivoFijoTangibleDto> findById(UUID id);

    Optional<ActivoFijoTangibleDto> findByNumeroInventario(String numeroInventario);

    Page<ActivoFijoTangibleDto> search(String query, UUID grupoId, UUID fincaId, Boolean activo, Pageable pageable);

    List<ActivoFijoTangibleDto> findByGrupo(UUID grupoId);

    List<ActivoFijoTangibleDto> findByFinca(UUID fincaId);

    /**
     * Dar de baja un activo fijo.
     */
    ActivoFijoTangibleDto darDeBaja(UUID id, String motivo);
}
