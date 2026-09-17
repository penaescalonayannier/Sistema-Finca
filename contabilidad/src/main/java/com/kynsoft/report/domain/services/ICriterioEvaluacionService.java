package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.CriterioEvaluacionDto;
import java.util.List;
import java.util.UUID;

public interface ICriterioEvaluacionService {
    List<CriterioEvaluacionDto> listar(boolean incluirInactivos);
    CriterioEvaluacionDto guardar(CriterioEvaluacionDto criterio);
    void desactivar(UUID id);
}
