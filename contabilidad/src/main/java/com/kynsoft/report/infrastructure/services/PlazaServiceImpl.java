package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.PlazaDto;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.services.IPlazaService;
import com.kynsoft.report.infrastructure.entity.AreaTrabajo;
import com.kynsoft.report.infrastructure.entity.Plaza;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.repository.command.PlazaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.command.TrabajadorWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AreaTrabajoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CargoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.GrupoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.PlazaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PlazaServiceImpl implements IPlazaService {
    private final PlazaWriteDataJPARepository writeRepository; private final PlazaReadDataJPARepository readRepository;
    private final FincaReadDataJPARepository fincaRepository; private final AreaTrabajoReadDataJPARepository areaRepository;
    private final GrupoReadDataJPARepository grupoRepository; private final CargoReadDataJPARepository cargoRepository;
    private final TrabajadorReadDataJPARepository trabajadorRepository; private final TrabajadorWriteDataJPARepository trabajadorWriteRepository;
    private final AuditoriaTransaccionalService auditoria;
    public PlazaServiceImpl(PlazaWriteDataJPARepository writeRepository, PlazaReadDataJPARepository readRepository,
                            FincaReadDataJPARepository fincaRepository, AreaTrabajoReadDataJPARepository areaRepository,
                            GrupoReadDataJPARepository grupoRepository, CargoReadDataJPARepository cargoRepository,
                            TrabajadorReadDataJPARepository trabajadorRepository, TrabajadorWriteDataJPARepository trabajadorWriteRepository,
                            AuditoriaTransaccionalService auditoria) {
        this.writeRepository=writeRepository; this.readRepository=readRepository; this.fincaRepository=fincaRepository; this.areaRepository=areaRepository;
        this.grupoRepository=grupoRepository; this.cargoRepository=cargoRepository; this.trabajadorRepository=trabajadorRepository; this.trabajadorWriteRepository=trabajadorWriteRepository; this.auditoria=auditoria;
    }
    @Override public PlazaDto create(PlazaDto dto) { if (dto.getId()==null) dto.setId(UUID.randomUUID()); validar(dto, null); Plaza p=writeRepository.save(new Plaza(dto)); PlazaDto result=enriquecer(p.toAggregate()); auditoria.registrarDespuesDeConfirmar(TipoAccion.CREATE,"PLAZA",p.getId(),"Creó plaza "+p.getCodigo(),null,result); return result; }
    @Override public PlazaDto update(UUID id, PlazaDto dto) { Plaza p=obtener(id); PlazaDto anterior=enriquecer(p.toAggregate()); dto.setId(id); if(dto.getFincaId()==null) dto.setFincaId(p.getFincaId()); validar(dto,id); p.aplicar(dto); PlazaDto result=enriquecer(writeRepository.save(p).toAggregate()); auditoria.registrarDespuesDeConfirmar(TipoAccion.UPDATE,"PLAZA",id,"Actualizó plaza "+p.getCodigo(),anterior,result); return result; }
    @Override @Transactional(readOnly = true) public PlazaDto findById(UUID id) { Plaza p=readRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Plaza no encontrada.")); TenantValidator.validateReadAccess(p.getFincaId()); return enriquecer(p.toAggregate()); }
    @Override @Transactional(readOnly = true) public List<PlazaDto> findByFinca(UUID fincaId) { TenantValidator.validateReadAccess(fincaId); return readRepository.findByFincaIdOrderByCodigo(fincaId).stream().map(Plaza::toAggregate).map(this::enriquecer).toList(); }
    @Override public void desactivar(UUID id) { Plaza p=obtener(id); PlazaDto anterior=enriquecer(p.toAggregate()); if(trabajadorRepository.findByPlazaIdAndActivoTrue(id).isPresent()) throw new IllegalArgumentException("No puede desactivar una plaza ocupada; desasigne primero al trabajador."); p.setActivo(false); writeRepository.save(p); auditoria.registrarDespuesDeConfirmar(TipoAccion.UPDATE,"PLAZA",id,"Desactivó plaza "+p.getCodigo(),anterior,enriquecer(p.toAggregate())); }
    @Override public PlazaDto asignarTrabajador(UUID plazaId, UUID trabajadorId) { Plaza plaza=obtener(plazaId); Trabajador trabajador=trabajadorWriteRepository.findById(trabajadorId).orElseThrow(()->new IllegalArgumentException("Trabajador no encontrado.")); validarAsignacion(plazaId,trabajador.getFincaId(),trabajador.getCargoId()); Trabajador actual=trabajadorRepository.findByPlazaIdAndActivoTrue(plazaId).orElse(null); if(actual != null && !actual.getId().equals(trabajadorId)) throw new IllegalArgumentException("La plaza ya está ocupada."); UUID anterior=trabajador.getPlazaId(); trabajador.setPlazaId(plazaId); trabajadorWriteRepository.save(trabajador); PlazaDto result=enriquecer(plaza.toAggregate()); auditoria.registrarDespuesDeConfirmar(TipoAccion.UPDATE,"PLAZA",plazaId,"Asignó trabajador a plaza "+plaza.getCodigo(),anterior,result); return result; }
    @Override public PlazaDto desasignarTrabajador(UUID plazaId) { Plaza plaza=obtener(plazaId); Trabajador trabajador=trabajadorRepository.findByPlazaIdAndActivoTrue(plazaId).orElseThrow(()->new IllegalArgumentException("La plaza ya está vacante.")); trabajador.setPlazaId(null); trabajadorWriteRepository.save(trabajador); PlazaDto result=enriquecer(plaza.toAggregate()); auditoria.registrarDespuesDeConfirmar(TipoAccion.UPDATE,"PLAZA",plazaId,"Liberó plaza "+plaza.getCodigo(),trabajador.getId(),result); return result; }
    @Override @Transactional(readOnly = true) public void validarAsignacion(UUID plazaId, UUID fincaId, UUID cargoId) { Plaza plaza=readRepository.findById(plazaId).orElseThrow(()->new IllegalArgumentException("Plaza no encontrada.")); TenantValidator.validateWriteAccess(fincaId); if(!Boolean.TRUE.equals(plaza.getActivo()) || !plaza.getFincaId().equals(fincaId) || !plaza.getCargoId().equals(cargoId)) throw new IllegalArgumentException("La plaza debe estar activa y corresponder a la finca y cargo del trabajador."); LocalDate hoy=LocalDate.now(); if((plaza.getFechaInicio()!=null && plaza.getFechaInicio().isAfter(hoy)) || (plaza.getFechaFin()!=null && plaza.getFechaFin().isBefore(hoy))) throw new IllegalArgumentException("La plaza no está vigente para la fecha actual."); }
    private Plaza obtener(UUID id) { Plaza p=writeRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Plaza no encontrada.")); TenantValidator.validateWriteAccess(p.getFincaId()); return p; }
    private PlazaDto enriquecer(PlazaDto dto) { trabajadorRepository.findByPlazaIdAndActivoTrue(dto.getId()).ifPresent(t->{dto.setTrabajadorId(t.getId());dto.setTrabajadorNombre(t.getNombre());}); return dto; }
    private void validar(PlazaDto dto, UUID actualId) { if(dto.getFincaId()==null || !fincaRepository.existsById(dto.getFincaId())) throw new IllegalArgumentException("La finca es obligatoria y debe existir."); TenantValidator.validateWriteAccess(dto.getFincaId()); if(dto.getCodigo()==null||dto.getCodigo().isBlank()||dto.getCargoId()==null||!cargoRepository.existsById(dto.getCargoId())) throw new IllegalArgumentException("Código y cargo válido son obligatorios."); if((actualId==null&&readRepository.existsByFincaIdAndCodigo(dto.getFincaId(),dto.getCodigo()))||(actualId!=null&&readRepository.findByFincaIdOrderByCodigo(dto.getFincaId()).stream().anyMatch(p->!p.getId().equals(actualId)&&p.getCodigo().equalsIgnoreCase(dto.getCodigo())))) throw new IllegalArgumentException("Ya existe una plaza con ese código en la finca."); if(dto.getFechaInicio()!=null&&dto.getFechaFin()!=null&&dto.getFechaFin().isBefore(dto.getFechaInicio())) throw new IllegalArgumentException("La fecha de fin no puede ser anterior al inicio."); if(dto.getAreaId()!=null){AreaTrabajo a=areaRepository.findById(dto.getAreaId()).orElseThrow(()->new IllegalArgumentException("Área no encontrada."));if(!a.getFincaId().equals(dto.getFincaId())||!Boolean.TRUE.equals(a.getActivo()))throw new IllegalArgumentException("El área debe estar activa y pertenecer a la finca.");} if(dto.getGrupoId()!=null&&!grupoRepository.existsById(dto.getGrupoId()))throw new IllegalArgumentException("Grupo no encontrado."); validarResponsable(dto.getResponsableId(),dto.getFincaId()); }
    private void validarResponsable(UUID id,UUID fincaId){if(id==null)return;Trabajador t=trabajadorRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Responsable no encontrado."));if(!Boolean.TRUE.equals(t.getActivo())||!fincaId.equals(t.getFincaId()))throw new IllegalArgumentException("El responsable debe estar activo y pertenecer a la finca.");}
}
