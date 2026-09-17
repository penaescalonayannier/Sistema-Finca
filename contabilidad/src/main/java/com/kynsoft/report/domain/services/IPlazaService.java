package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.PlazaDto;
import java.util.List;
import java.util.UUID;

public interface IPlazaService {
    PlazaDto create(PlazaDto dto);
    PlazaDto update(UUID id, PlazaDto dto);
    PlazaDto findById(UUID id);
    List<PlazaDto> findByFinca(UUID fincaId);
    void desactivar(UUID id);
    PlazaDto asignarTrabajador(UUID plazaId, UUID trabajadorId);
    PlazaDto desasignarTrabajador(UUID plazaId);
    void validarAsignacion(UUID plazaId, UUID fincaId, UUID cargoId);
}
