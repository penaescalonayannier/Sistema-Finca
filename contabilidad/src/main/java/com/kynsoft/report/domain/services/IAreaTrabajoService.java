package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.AreaTrabajoDto;
import java.util.List;
import java.util.UUID;

public interface IAreaTrabajoService {
    AreaTrabajoDto create(AreaTrabajoDto dto);
    AreaTrabajoDto update(UUID id, AreaTrabajoDto dto);
    AreaTrabajoDto findById(UUID id);
    List<AreaTrabajoDto> findByFinca(UUID fincaId);
    void desactivar(UUID id);
}
