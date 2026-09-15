package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.ActivoAnimalDto;
import com.kynsoft.report.domain.dto.enums.CategoriaAnimal;
import com.kynsoft.report.domain.dto.enums.TipoGanado;
import com.kynsoft.report.domain.services.IActivoAnimalService;
import com.kynsoft.report.infrastructure.entity.ActivoAnimal;
import com.kynsoft.report.infrastructure.repository.command.ActivoAnimalWriteRepository;
import com.kynsoft.report.infrastructure.repository.query.ActivoAnimalReadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Activos Animales.
 * Tabla independiente según NCC No. 7 (Resolución 1038/2017 MFP) - Grupo 08
 */
@Service
public class ActivoAnimalServiceImpl implements IActivoAnimalService {

    private final ActivoAnimalReadRepository readRepository;
    private final ActivoAnimalWriteRepository writeRepository;

    public ActivoAnimalServiceImpl(ActivoAnimalReadRepository readRepository,
                                    ActivoAnimalWriteRepository writeRepository) {
        this.readRepository = readRepository;
        this.writeRepository = writeRepository;
    }

    @Override
    @Transactional
    public ActivoAnimalDto create(ActivoAnimalDto dto) {
        ActivoAnimal animal = new ActivoAnimal(dto);
        if (animal.getId() == null) {
            animal.setId(UUID.randomUUID());
        }
        animal = writeRepository.save(animal);
        return animal.toAggregate();
    }

    @Override
    @Transactional
    public ActivoAnimalDto update(ActivoAnimalDto dto) {
        ActivoAnimal animal = readRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Animal no encontrado: " + dto.getId()));

        animal.setNumeroInventario(dto.getNumeroInventario());
        animal.setCodigoArete(dto.getCodigoArete());
        animal.setHierro(dto.getHierro());
        animal.setCategoria(dto.getCategoria());
        animal.setTipoGanado(dto.getTipoGanado());
        animal.setFincaId(dto.getFincaId());
        animal.setValorAdquisicion(dto.getValorAdquisicion());
        animal.setDepreciacionAcumulada(dto.getDepreciacionAcumulada());
        animal.setValorResidual(dto.getValorResidual());
        animal.setValorTasacion(dto.getValorTasacion());
        animal.setAniosVida(dto.getAniosVida());
        animal.setPesoPromedio(dto.getPesoPromedio());
        animal.setDestino(dto.getDestino());
        animal.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        animal = writeRepository.save(animal);
        return animal.toAggregate();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        writeRepository.deleteById(id);
    }

    @Override
    public Optional<ActivoAnimalDto> findById(UUID id) {
        return readRepository.findById(id)
                .map(ActivoAnimal::toAggregate);
    }

    @Override
    public Page<ActivoAnimalDto> search(String query, TipoGanado tipoGanado, CategoriaAnimal categoria,
                                         UUID fincaId, Pageable pageable) {
        return readRepository.search(query, tipoGanado, categoria, fincaId, pageable)
                .map(ActivoAnimal::toAggregate);
    }

    @Override
    public List<ActivoAnimalDto> findByTipoGanado(TipoGanado tipoGanado) {
        return readRepository.findByTipoGanado(tipoGanado).stream()
                .map(ActivoAnimal::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivoAnimalDto> findByCategoria(CategoriaAnimal categoria) {
        return readRepository.findByCategoria(categoria).stream()
                .map(ActivoAnimal::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivoAnimalDto> findByFinca(UUID fincaId) {
        return readRepository.findByFincaId(fincaId).stream()
                .map(ActivoAnimal::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResumenAnimalDto> getResumenPorCategoria(UUID fincaId) {
        List<Object[]> results = readRepository.getResumenPorCategoria(fincaId);
        List<ResumenAnimalDto> resumen = new ArrayList<>();

        for (Object[] row : results) {
            CategoriaAnimal categoria = (CategoriaAnimal) row[0];
            Long cantidad = (Long) row[1];
            BigDecimal valorTotal = (BigDecimal) row[2];
            resumen.add(new ResumenAnimalDto(categoria, cantidad, valorTotal));
        }

        return resumen;
    }
}
