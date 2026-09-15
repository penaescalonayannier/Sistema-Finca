package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.ActivoAnimalDto;
import com.kynsoft.report.domain.dto.enums.CategoriaAnimal;
import com.kynsoft.report.domain.dto.enums.TipoGanado;
import com.kynsoft.report.infrastructure.services.ResumenAnimalDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestión de Activos Animales (Grupo 08).
 *
 * Referencia: NCC No. 7 (Resolución 1038/2017 MFP) - Grupo 08
 */
public interface IActivoAnimalService {

    ActivoAnimalDto create(ActivoAnimalDto dto);

    ActivoAnimalDto update(ActivoAnimalDto dto);

    void delete(UUID id);

    Optional<ActivoAnimalDto> findById(UUID id);

    Page<ActivoAnimalDto> search(String query, TipoGanado tipoGanado, CategoriaAnimal categoria,
                                  UUID fincaId, Pageable pageable);

    List<ActivoAnimalDto> findByTipoGanado(TipoGanado tipoGanado);

    List<ActivoAnimalDto> findByCategoria(CategoriaAnimal categoria);

    List<ActivoAnimalDto> findByFinca(UUID fincaId);

    /**
     * Resumen de inventario de animales por categoría.
     */
    List<ResumenAnimalDto> getResumenPorCategoria(UUID fincaId);
}
