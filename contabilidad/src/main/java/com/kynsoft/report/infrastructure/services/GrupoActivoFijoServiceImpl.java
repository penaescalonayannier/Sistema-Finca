package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.GrupoActivoFijoDto;
import com.kynsoft.report.domain.services.IGrupoActivoFijoService;
import com.kynsoft.report.infrastructure.entity.GrupoActivoFijo;
import com.kynsoft.report.infrastructure.repository.command.GrupoActivoFijoWriteRepository;
import com.kynsoft.report.infrastructure.repository.query.GrupoActivoFijoReadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Grupos de Activos Fijos.
 *
 * Referencia: NCC No. 7 (Resolución 1038/2017 MFP)
 */
@Service
public class GrupoActivoFijoServiceImpl implements IGrupoActivoFijoService {

    private final GrupoActivoFijoReadRepository readRepository;
    private final GrupoActivoFijoWriteRepository writeRepository;

    public GrupoActivoFijoServiceImpl(GrupoActivoFijoReadRepository readRepository,
                                       GrupoActivoFijoWriteRepository writeRepository) {
        this.readRepository = readRepository;
        this.writeRepository = writeRepository;
    }

    @Override
    @Transactional
    public GrupoActivoFijoDto create(GrupoActivoFijoDto dto) {
        if (dto.getId() == null) {
            dto.setId(UUID.randomUUID());
        }
        GrupoActivoFijo entity = new GrupoActivoFijo(dto);
        entity = writeRepository.save(entity);
        return entity.toAggregate();
    }

    @Override
    @Transactional
    public GrupoActivoFijoDto update(GrupoActivoFijoDto dto) {
        GrupoActivoFijo entity = new GrupoActivoFijo(dto);
        entity = writeRepository.save(entity);
        return entity.toAggregate();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        writeRepository.deleteById(id);
    }

    @Override
    public Optional<GrupoActivoFijoDto> findById(UUID id) {
        return readRepository.findById(id)
                .map(GrupoActivoFijo::toAggregate);
    }

    @Override
    public Optional<GrupoActivoFijoDto> findByCodigo(String codigo) {
        return readRepository.findByCodigo(codigo)
                .map(GrupoActivoFijo::toAggregate);
    }

    @Override
    public List<GrupoActivoFijoDto> findAll() {
        return readRepository.findAllByOrderByCodigoAsc().stream()
                .map(GrupoActivoFijo::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<GrupoActivoFijoDto> findAllActivos() {
        return readRepository.findByActivoTrue().stream()
                .map(GrupoActivoFijo::toAggregate)
                .collect(Collectors.toList());
    }
}
