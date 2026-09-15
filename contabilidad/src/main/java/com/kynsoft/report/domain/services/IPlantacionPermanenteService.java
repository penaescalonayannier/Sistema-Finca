package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.PlantacionPermanenteDto;
import com.kynsoft.report.domain.dto.enums.TipoPlantacion;
import com.kynsoft.report.infrastructure.services.ResumenPlantacionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestión de Plantaciones Permanentes (Grupos 12 y 13).
 *
 * Referencia: NCC No. 7 (Resolución 1038/2017 MFP) - Grupos 12 y 13
 */
public interface IPlantacionPermanenteService {

    PlantacionPermanenteDto create(PlantacionPermanenteDto dto);

    PlantacionPermanenteDto update(PlantacionPermanenteDto dto);

    void delete(UUID id);

    Optional<PlantacionPermanenteDto> findById(UUID id);

    Page<PlantacionPermanenteDto> search(String query, TipoPlantacion tipoPlantacion,
                                          Integer bloque, UUID fincaId, Pageable pageable);

    List<PlantacionPermanenteDto> findByBloque(Integer bloque);

    List<PlantacionPermanenteDto> findByTipoPlantacion(TipoPlantacion tipoPlantacion);

    List<PlantacionPermanenteDto> findByFinca(UUID fincaId);

    /**
     * Resumen de área por tipo de plantación.
     */
    List<ResumenPlantacionDto> getResumenPorTipo(UUID fincaId);
}
