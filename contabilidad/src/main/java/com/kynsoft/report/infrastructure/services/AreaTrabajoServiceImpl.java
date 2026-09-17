package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.AreaTrabajoDto;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.services.IAreaTrabajoService;
import com.kynsoft.report.infrastructure.entity.AreaTrabajo;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.repository.command.AreaTrabajoWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AreaTrabajoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AreaTrabajoServiceImpl implements IAreaTrabajoService {
    private final AreaTrabajoWriteDataJPARepository writeRepository;
    private final AreaTrabajoReadDataJPARepository readRepository;
    private final FincaReadDataJPARepository fincaRepository;
    private final TrabajadorReadDataJPARepository trabajadorRepository;
    private final AuditoriaTransaccionalService auditoria;

    public AreaTrabajoServiceImpl(AreaTrabajoWriteDataJPARepository writeRepository,
                                  AreaTrabajoReadDataJPARepository readRepository,
                                  FincaReadDataJPARepository fincaRepository,
                                  TrabajadorReadDataJPARepository trabajadorRepository,
                                  AuditoriaTransaccionalService auditoria) {
        this.writeRepository = writeRepository; this.readRepository = readRepository;
        this.fincaRepository = fincaRepository; this.trabajadorRepository = trabajadorRepository; this.auditoria = auditoria;
    }

    @Override public AreaTrabajoDto create(AreaTrabajoDto dto) {
        if (dto.getId() == null) dto.setId(UUID.randomUUID());
        validar(dto, null);
        AreaTrabajo saved = writeRepository.save(new AreaTrabajo(dto));
        AreaTrabajoDto result = saved.toAggregate();
        auditoria.registrarDespuesDeConfirmar(TipoAccion.CREATE, "AREA_TRABAJO", saved.getId(),
                "Creó área de trabajo " + saved.getCodigo(), null, result);
        return result;
    }
    @Override public AreaTrabajoDto update(UUID id, AreaTrabajoDto dto) {
        AreaTrabajo current = obtener(id); AreaTrabajoDto anterior = current.toAggregate();
        dto.setId(id); if (dto.getFincaId() == null) dto.setFincaId(current.getFincaId());
        validar(dto, id); current.aplicar(dto); AreaTrabajoDto result = writeRepository.save(current).toAggregate();
        auditoria.registrarDespuesDeConfirmar(TipoAccion.UPDATE, "AREA_TRABAJO", id,
                "Actualizó área de trabajo " + result.getCodigo(), anterior, result);
        return result;
    }
    @Override @Transactional(readOnly = true) public AreaTrabajoDto findById(UUID id) {
        AreaTrabajo area = readRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Área de trabajo no encontrada."));
        TenantValidator.validateReadAccess(area.getFincaId()); return area.toAggregate();
    }
    @Override @Transactional(readOnly = true) public List<AreaTrabajoDto> findByFinca(UUID fincaId) {
        TenantValidator.validateReadAccess(fincaId);
        return readRepository.findByFincaIdOrderByNombre(fincaId).stream().map(AreaTrabajo::toAggregate).toList();
    }
    @Override public void desactivar(UUID id) {
        AreaTrabajo area = obtener(id); AreaTrabajoDto anterior = area.toAggregate();
        TenantValidator.validateWriteAccess(area.getFincaId()); area.setActivo(false); writeRepository.save(area);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.UPDATE, "AREA_TRABAJO", id,
                "Desactivó área de trabajo " + area.getCodigo(), anterior, area.toAggregate());
    }
    private AreaTrabajo obtener(UUID id) {
        AreaTrabajo area = writeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Área de trabajo no encontrada."));
        TenantValidator.validateWriteAccess(area.getFincaId()); return area;
    }
    private void validar(AreaTrabajoDto dto, UUID actualId) {
        if (dto.getFincaId() == null || !fincaRepository.existsById(dto.getFincaId())) throw new IllegalArgumentException("La finca es obligatoria y debe existir.");
        TenantValidator.validateWriteAccess(dto.getFincaId());
        if (dto.getCodigo() == null || dto.getCodigo().isBlank() || dto.getNombre() == null || dto.getNombre().isBlank() || dto.getTipo() == null) throw new IllegalArgumentException("Código, nombre y tipo son obligatorios.");
        if ((actualId == null && readRepository.existsByFincaIdAndCodigo(dto.getFincaId(), dto.getCodigo())) ||
                (actualId != null && readRepository.findByFincaIdOrderByNombre(dto.getFincaId()).stream().anyMatch(a -> !a.getId().equals(actualId) && a.getCodigo().equalsIgnoreCase(dto.getCodigo())))) throw new IllegalArgumentException("Ya existe un área con ese código en la finca.");
        if (dto.getFechaInicio() != null && dto.getFechaFin() != null && dto.getFechaFin().isBefore(dto.getFechaInicio())) throw new IllegalArgumentException("La fecha de fin no puede ser anterior al inicio.");
        if (dto.getAreaPadreId() != null) {
            if (dto.getAreaPadreId().equals(actualId)) throw new IllegalArgumentException("Un área no puede ser su propia área superior.");
            AreaTrabajo padre = readRepository.findById(dto.getAreaPadreId()).orElseThrow(() -> new IllegalArgumentException("Área superior no encontrada."));
            if (!padre.getFincaId().equals(dto.getFincaId()) || !Boolean.TRUE.equals(padre.getActivo())) throw new IllegalArgumentException("El área superior debe estar activa y pertenecer a la misma finca.");
        }
        validarResponsable(dto.getResponsableId(), dto.getFincaId());
    }
    private void validarResponsable(UUID trabajadorId, UUID fincaId) {
        if (trabajadorId == null) return;
        Trabajador trabajador = trabajadorRepository.findById(trabajadorId).orElseThrow(() -> new IllegalArgumentException("Responsable no encontrado."));
        if (!Boolean.TRUE.equals(trabajador.getActivo()) || !fincaId.equals(trabajador.getFincaId())) throw new IllegalArgumentException("El responsable debe estar activo y pertenecer a la finca.");
    }
}
