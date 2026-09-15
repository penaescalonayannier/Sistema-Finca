package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.ActivoFijoTangibleDto;
import com.kynsoft.report.domain.services.IActivoFijoTangibleService;
import com.kynsoft.report.infrastructure.entity.ActivoFijoTangible;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.GrupoActivoFijo;
import com.kynsoft.report.infrastructure.repository.command.ActivoFijoTangibleWriteRepository;
import com.kynsoft.report.infrastructure.repository.query.ActivoFijoTangibleReadRepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.GrupoActivoFijoReadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Activos Fijos Tangibles.
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP)
 * - Resolución 60/2011 CGR: Control interno
 */
@Service
public class ActivoFijoTangibleServiceImpl implements IActivoFijoTangibleService {

    private final ActivoFijoTangibleReadRepository readRepository;
    private final ActivoFijoTangibleWriteRepository writeRepository;
    private final GrupoActivoFijoReadRepository grupoRepository;
    private final FincaReadDataJPARepository fincaRepository;

    public ActivoFijoTangibleServiceImpl(ActivoFijoTangibleReadRepository readRepository,
                                          ActivoFijoTangibleWriteRepository writeRepository,
                                          GrupoActivoFijoReadRepository grupoRepository,
                                          FincaReadDataJPARepository fincaRepository) {
        this.readRepository = readRepository;
        this.writeRepository = writeRepository;
        this.grupoRepository = grupoRepository;
        this.fincaRepository = fincaRepository;
    }

    @Override
    @Transactional
    public ActivoFijoTangibleDto create(ActivoFijoTangibleDto dto) {
        if (dto.getId() == null) {
            dto.setId(UUID.randomUUID());
        }

        ActivoFijoTangible entity = new ActivoFijoTangible(dto);

        // Establecer relaciones
        if (dto.getGrupoId() != null) {
            GrupoActivoFijo grupo = grupoRepository.findById(dto.getGrupoId())
                    .orElseThrow(() -> new RuntimeException("Grupo no encontrado: " + dto.getGrupoId()));
            entity.setGrupo(grupo);
        }

        if (dto.getFincaId() != null) {
            Finca finca = fincaRepository.findById(dto.getFincaId())
                    .orElseThrow(() -> new RuntimeException("Finca no encontrada: " + dto.getFincaId()));
            entity.setFinca(finca);
        }

        entity = writeRepository.save(entity);
        return entity.toAggregate();
    }

    @Override
    @Transactional
    public ActivoFijoTangibleDto update(ActivoFijoTangibleDto dto) {
        ActivoFijoTangible existing = readRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Activo no encontrado: " + dto.getId()));

        existing.setNumeroInventario(dto.getNumeroInventario());
        existing.setDescripcion(dto.getDescripcion());
        existing.setValorAdquisicion(dto.getValorAdquisicion());
        existing.setDepreciacionAcumulada(dto.getDepreciacionAcumulada());
        existing.setEstadoTecnicoPorcentaje(dto.getEstadoTecnicoPorcentaje());
        existing.setValorTasacion(dto.getValorTasacion());
        existing.setFechaAdquisicion(dto.getFechaAdquisicion());
        existing.setDestino(dto.getDestino());
        existing.setObservaciones(dto.getObservaciones());
        existing.setActivo(dto.getActivo());

        if (dto.getGrupoId() != null) {
            GrupoActivoFijo grupo = grupoRepository.findById(dto.getGrupoId())
                    .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
            existing.setGrupo(grupo);
        }

        if (dto.getFincaId() != null) {
            Finca finca = fincaRepository.findById(dto.getFincaId())
                    .orElseThrow(() -> new RuntimeException("Finca no encontrada"));
            existing.setFinca(finca);
        }

        existing = writeRepository.save(existing);
        return existing.toAggregate();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        writeRepository.deleteById(id);
    }

    @Override
    public Optional<ActivoFijoTangibleDto> findById(UUID id) {
        return readRepository.findById(id)
                .map(ActivoFijoTangible::toAggregate);
    }

    @Override
    public Optional<ActivoFijoTangibleDto> findByNumeroInventario(String numeroInventario) {
        return readRepository.findByNumeroInventario(numeroInventario)
                .map(ActivoFijoTangible::toAggregate);
    }

    @Override
    public Page<ActivoFijoTangibleDto> search(String query, UUID grupoId, UUID fincaId,
                                               Boolean activo, Pageable pageable) {
        return readRepository.search(query, grupoId, fincaId, activo, pageable)
                .map(ActivoFijoTangible::toAggregate);
    }

    @Override
    public List<ActivoFijoTangibleDto> findByGrupo(UUID grupoId) {
        return readRepository.findByGrupoId(grupoId).stream()
                .map(ActivoFijoTangible::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivoFijoTangibleDto> findByFinca(UUID fincaId) {
        return readRepository.findByFincaId(fincaId).stream()
                .map(ActivoFijoTangible::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ActivoFijoTangibleDto darDeBaja(UUID id, String motivo) {
        ActivoFijoTangible entity = readRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activo no encontrado: " + id));

        entity.setFechaBaja(LocalDate.now());
        entity.setActivo(false);
        entity.setObservaciones((entity.getObservaciones() != null ? entity.getObservaciones() + " | " : "")
                + "BAJA: " + motivo);

        entity = writeRepository.save(entity);
        return entity.toAggregate();
    }
}
