package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.ActualizarExpedienteDisciplinarioRequest;
import com.kynsoft.report.domain.dto.AnularExpedienteDisciplinarioRequest;
import com.kynsoft.report.domain.dto.CrearExpedienteDisciplinarioRequest;
import com.kynsoft.report.domain.dto.EstadoExpedienteDisciplinario;
import com.kynsoft.report.domain.dto.ExpedienteDisciplinarioDto;
import com.kynsoft.report.domain.dto.ResolverExpedienteDisciplinarioRequest;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.services.IExpedienteDisciplinarioService;
import com.kynsoft.report.infrastructure.entity.ExpedienteDisciplinario;
import com.kynsoft.report.infrastructure.entity.Finca;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.repository.command.ExpedienteDisciplinarioWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ExpedienteDisciplinarioReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantValidator;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Expediente documental de disciplina. No liquida salarios, no cambia asistencia
 * y nunca borra registros: una corrección se materializa mediante ANULADA.
 */
@Service
@Transactional
public class ExpedienteDisciplinarioServiceImpl implements IExpedienteDisciplinarioService {
    private final ExpedienteDisciplinarioWriteDataJPARepository writeRepository;
    private final ExpedienteDisciplinarioReadDataJPARepository readRepository;
    private final TrabajadorReadDataJPARepository trabajadorRepository;
    private final FincaReadDataJPARepository fincaRepository;
    private final AuditoriaTransaccionalService auditoria;

    public ExpedienteDisciplinarioServiceImpl(ExpedienteDisciplinarioWriteDataJPARepository writeRepository,
                                              ExpedienteDisciplinarioReadDataJPARepository readRepository,
                                              TrabajadorReadDataJPARepository trabajadorRepository,
                                              FincaReadDataJPARepository fincaRepository,
                                              AuditoriaTransaccionalService auditoria) {
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.fincaRepository = fincaRepository;
        this.auditoria = auditoria;
    }

    @Override public UUID crear(CrearExpedienteDisciplinarioRequest request) {
        if (request == null) throw new IllegalArgumentException("Los datos del expediente son obligatorios.");
        validarBase(request.getFincaId(), request.getTrabajadorId(), request.getTipo(), request.getFecha(), request.getDescripcion());
        validarAprobador(request.getFincaId(), request.getAprobadorId());
        LocalDateTime ahora = LocalDateTime.now();
        ExpedienteDisciplinario expediente = new ExpedienteDisciplinario();
        expediente.setId(UUID.randomUUID()); expediente.setFincaId(request.getFincaId()); expediente.setTrabajadorId(request.getTrabajadorId());
        expediente.setAprobadorId(request.getAprobadorId()); expediente.setTipo(request.getTipo()); expediente.setFecha(request.getFecha());
        expediente.setDescripcion(requerido(request.getDescripcion(), "La descripción es obligatoria."));
        expediente.setEvidencia(texto(request.getEvidencia())); expediente.setObservaciones(texto(request.getObservaciones()));
        expediente.setEstado(EstadoExpedienteDisciplinario.BORRADOR); expediente.setCreadoEn(ahora); expediente.setActualizadoEn(ahora);
        writeRepository.save(expediente);
        auditar(TipoAccion.CREATE, expediente, "Creación de expediente disciplinario", null, resumen(expediente));
        return expediente.getId();
    }

    @Override public void actualizar(UUID id, ActualizarExpedienteDisciplinarioRequest request) {
        if (request == null) throw new IllegalArgumentException("Los datos de actualización son obligatorios.");
        ExpedienteDisciplinario e = bloqueado(id); soloBorrador(e, "modificar");
        Map<String, Object> anterior = resumen(e);
        validarBase(e.getFincaId(), e.getTrabajadorId(), request.getTipo(), request.getFecha(), request.getDescripcion());
        validarAprobador(e.getFincaId(), request.getAprobadorId());
        e.setAprobadorId(request.getAprobadorId()); e.setTipo(request.getTipo()); e.setFecha(request.getFecha());
        e.setDescripcion(requerido(request.getDescripcion(), "La descripción es obligatoria.")); e.setEvidencia(texto(request.getEvidencia()));
        e.setObservaciones(texto(request.getObservaciones())); e.setActualizadoEn(LocalDateTime.now()); writeRepository.save(e);
        auditar(TipoAccion.UPDATE, e, "Actualización de borrador disciplinario", anterior, resumen(e));
    }

    @Override public void notificar(UUID id) {
        ExpedienteDisciplinario e = bloqueado(id); soloBorrador(e, "notificar");
        if (e.getAprobadorId() == null) throw new IllegalArgumentException("Debe asignar un aprobador antes de notificar el expediente.");
        Map<String, Object> anterior = resumen(e); e.setEstado(EstadoExpedienteDisciplinario.NOTIFICADA);
        e.setFechaNotificacion(LocalDateTime.now()); e.setActualizadoEn(LocalDateTime.now()); writeRepository.save(e);
        auditar(TipoAccion.UPDATE, e, "Notificación de expediente disciplinario", anterior, resumen(e));
    }

    @Override public void resolver(UUID id, ResolverExpedienteDisciplinarioRequest request) {
        if (request == null) throw new IllegalArgumentException("La resolución es obligatoria.");
        ExpedienteDisciplinario e = bloqueado(id);
        if (e.getEstado() != EstadoExpedienteDisciplinario.NOTIFICADA) throw new IllegalArgumentException("Solo un expediente notificado puede resolverse.");
        if (request.getAprobadorId() != null) { validarAprobador(e.getFincaId(), request.getAprobadorId()); e.setAprobadorId(request.getAprobadorId()); }
        if (e.getAprobadorId() == null) throw new IllegalArgumentException("Debe asignar un aprobador antes de resolver el expediente.");
        Map<String, Object> anterior = resumen(e); e.setMedida(requerido(request.getMedida(), "La medida aplicada es obligatoria."));
        e.setResolucion(requerido(request.getResolucion(), "La resolución es obligatoria.")); e.setObservaciones(texto(request.getObservaciones()));
        e.setEstado(EstadoExpedienteDisciplinario.RESUELTA); e.setFechaResolucion(LocalDateTime.now()); e.setActualizadoEn(LocalDateTime.now()); writeRepository.save(e);
        auditar(TipoAccion.UPDATE, e, "Resolución de expediente disciplinario", anterior, resumen(e));
    }

    @Override public void anular(UUID id, AnularExpedienteDisciplinarioRequest request) {
        ExpedienteDisciplinario e = bloqueado(id);
        if (e.getEstado() == EstadoExpedienteDisciplinario.ANULADA || e.getEstado() == EstadoExpedienteDisciplinario.RESUELTA) throw new IllegalArgumentException("El expediente no puede anularse en su estado actual.");
        String motivo = request == null ? null : requerido(request.getMotivo(), "El motivo de anulación es obligatorio.");
        Map<String, Object> anterior = resumen(e); e.setEstado(EstadoExpedienteDisciplinario.ANULADA); e.setMotivoAnulacion(motivo);
        e.setFechaAnulacion(LocalDateTime.now()); e.setActualizadoEn(LocalDateTime.now()); writeRepository.save(e);
        auditar(TipoAccion.DELETE, e, "Anulación de expediente disciplinario", anterior, resumen(e));
    }

    @Override @Transactional(Transactional.TxType.SUPPORTS) public ExpedienteDisciplinarioDto detalle(UUID id) { return aDto(leer(id)); }
    @Override @Transactional(Transactional.TxType.SUPPORTS) public List<ExpedienteDisciplinarioDto> listar(UUID fincaId, UUID trabajadorId, LocalDate desde, LocalDate hasta, boolean incluirAnulados) {
        if (fincaId == null) throw new IllegalArgumentException("La finca es obligatoria."); TenantValidator.validateReadAccess(fincaId);
        if (desde != null && hasta != null && desde.isAfter(hasta)) throw new IllegalArgumentException("El período de consulta no es válido.");
        if (trabajadorId != null) trabajadorDeFinca(trabajadorId, fincaId);
        return readRepository.buscar(fincaId, trabajadorId, desde, hasta, incluirAnulados).stream().map(this::aDto).toList();
    }

    private ExpedienteDisciplinario bloqueado(UUID id) { ExpedienteDisciplinario e = writeRepository.findByIdForUpdate(id).orElseThrow(() -> new IllegalArgumentException("Expediente disciplinario no encontrado.")); TenantValidator.validateWriteAccess(e.getFincaId()); return e; }
    private ExpedienteDisciplinario leer(UUID id) { ExpedienteDisciplinario e = readRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Expediente disciplinario no encontrado.")); TenantValidator.validateReadAccess(e.getFincaId()); return e; }
    private void validarBase(UUID fincaId, UUID trabajadorId, Object tipo, LocalDate fecha, String descripcion) { if (fincaId == null || trabajadorId == null || tipo == null || fecha == null) throw new IllegalArgumentException("Finca, trabajador, tipo y fecha son obligatorios."); TenantValidator.validateWriteAccess(fincaId); requireFinca(fincaId); trabajadorDeFinca(trabajadorId, fincaId); requerido(descripcion, "La descripción es obligatoria."); }
    private void validarAprobador(UUID fincaId, UUID aprobadorId) { if (aprobadorId != null) trabajadorDeFinca(aprobadorId, fincaId); }
    private Finca requireFinca(UUID id) { return fincaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Finca no encontrada.")); }
    private Trabajador trabajadorDeFinca(UUID id, UUID fincaId) { Trabajador t = trabajadorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado.")); if (!fincaId.equals(t.getFincaId())) throw new IllegalArgumentException("El trabajador debe pertenecer a la finca del expediente."); return t; }
    private void soloBorrador(ExpedienteDisciplinario e, String accion) { if (e.getEstado() != EstadoExpedienteDisciplinario.BORRADOR) throw new IllegalArgumentException("Solo un expediente en borrador puede " + accion + "se."); }
    private ExpedienteDisciplinarioDto aDto(ExpedienteDisciplinario e) { Finca f = requireFinca(e.getFincaId()); Trabajador t = trabajadorDeFinca(e.getTrabajadorId(), e.getFincaId()); Trabajador a = e.getAprobadorId() == null ? null : trabajadorDeFinca(e.getAprobadorId(), e.getFincaId()); return ExpedienteDisciplinarioDto.builder().id(e.getId()).fincaId(e.getFincaId()).fincaNombre(f.getName()).trabajadorId(e.getTrabajadorId()).trabajadorNombre(t.getNombre()).aprobadorId(e.getAprobadorId()).aprobadorNombre(a == null ? null : a.getNombre()).tipo(e.getTipo()).estado(e.getEstado()).fecha(e.getFecha()).descripcion(e.getDescripcion()).evidencia(e.getEvidencia()).observaciones(e.getObservaciones()).medida(e.getMedida()).resolucion(e.getResolucion()).fechaNotificacion(e.getFechaNotificacion()).fechaResolucion(e.getFechaResolucion()).fechaAnulacion(e.getFechaAnulacion()).motivoAnulacion(e.getMotivoAnulacion()).creadoEn(e.getCreadoEn()).actualizadoEn(e.getActualizadoEn()).build(); }
    private void auditar(TipoAccion accion, ExpedienteDisciplinario e, String descripcion, Object anterior, Object nuevo) { auditoria.registrarDespuesDeConfirmar(accion, "EXPEDIENTE_DISCIPLINARIO", e.getId(), descripcion, anterior, nuevo); }
    private Map<String, Object> resumen(ExpedienteDisciplinario e) { return Map.of("estado", e.getEstado().name(), "trabajadorId", e.getTrabajadorId().toString(), "fecha", e.getFecha().toString(), "tipo", e.getTipo().name(), "medida", e.getMedida() == null ? "" : e.getMedida()); }
    private String requerido(String valor, String mensaje) { String t = texto(valor); if (t == null) throw new IllegalArgumentException(mensaje); return t; }
    private String texto(String valor) { return valor == null || valor.trim().isEmpty() ? null : valor.trim(); }
}
