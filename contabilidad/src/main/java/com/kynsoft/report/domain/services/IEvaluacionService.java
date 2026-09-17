package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.EvaluacionDto;
import com.kynsoft.report.domain.dto.EstadoEvaluacion;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IEvaluacionService {

    void create(EvaluacionDto object);

    void update(EvaluacionDto object);

    void delete(UUID id);

    EvaluacionDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<EvaluacionDto> findByMesAndYear(String mes, Integer year);

    List<EvaluacionDto> findByYearAndMeses(Integer year, List<String> meses);

    List<String> findDistinctMesesByYear(Integer year);

    List<Integer> findDistinctYears();

    Optional<EvaluacionDto> findByTrabajadorAndMesAndYear(UUID trabajadorId, String mes, Integer year);

    EvaluacionDto cambiarEstado(UUID id, EstadoEvaluacion estado, String constanciaJefe,
            String constanciaTrabajador, String observacionesCierre);
}
