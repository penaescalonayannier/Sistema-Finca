package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.HistorialSalarioDto;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.services.IHistorialSalarioService;
import com.kynsoft.report.infrastructure.entity.HistorialSalario;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.repository.command.HistorialSalarioWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.HistorialSalarioReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantValidator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Conserva condiciones salariales individuales; no calcula, contabiliza ni paga nómina.
 * Un cambio se registra como una nueva vigencia, nunca sobrescribiendo la anterior.
 */
@Service
@Transactional
public class HistorialSalarioServiceImpl implements IHistorialSalarioService {
    private static final String ACTIVO = "ACTIVO";
    private static final String ANULADO = "ANULADO";
    private final HistorialSalarioWriteDataJPARepository write;
    private final HistorialSalarioReadDataJPARepository read;
    private final TrabajadorReadDataJPARepository trabajadores;
    private final AuditoriaTransaccionalService auditoria;

    public HistorialSalarioServiceImpl(HistorialSalarioWriteDataJPARepository write,
            HistorialSalarioReadDataJPARepository read, TrabajadorReadDataJPARepository trabajadores,
            AuditoriaTransaccionalService auditoria) {
        this.write = write;
        this.read = read;
        this.trabajadores = trabajadores;
        this.auditoria = auditoria;
    }

    @Override
    public UUID registrar(HistorialSalarioDto solicitud) {
        if (solicitud == null || solicitud.getTrabajadorId() == null) {
            throw new IllegalArgumentException("Debe indicar el trabajador.");
        }
        Trabajador trabajador = trabajadores.findById(solicitud.getTrabajadorId())
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado."));
        validarEscritura(trabajador.getFincaId());
        validar(solicitud);
        if (read.existsByTrabajadorIdAndFechaVigencia(trabajador.getId(), solicitud.getFechaVigencia())) {
            throw new IllegalArgumentException("Ya existe una vigencia salarial para esa fecha.");
        }
        HistorialSalario registro = new HistorialSalario();
        registro.setId(UUID.randomUUID());
        registro.setTrabajadorId(trabajador.getId());
        registro.setFincaId(trabajador.getFincaId());
        registro.setCargoId(solicitud.getCargoId() == null ? trabajador.getCargoId() : solicitud.getCargoId());
        registro.setFechaVigencia(solicitud.getFechaVigencia());
        registro.setSalarioEscala(solicitud.getSalarioEscala());
        registro.setAnticipoDiario(ceroSiNulo(solicitud.getAnticipoDiario()));
        registro.setTasa(ceroSiNulo(solicitud.getTasa()));
        registro.setMotivo(solicitud.getMotivo().trim());
        registro.setEstado(ACTIVO);
        registro.setAutorizadoPorId(TenantContext.getUsuarioId());
        write.save(registro);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.CREATE, "HISTORIAL_SALARIAL", registro.getId(),
                "Registrada vigencia salarial individual", null, resumen(registro));
        return registro.getId();
    }

    @Override
    public void anular(UUID id, String motivo) {
        HistorialSalario registro = read.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vigencia salarial no encontrada."));
        validarEscritura(registro.getFincaId());
        if (ANULADO.equals(registro.getEstado())) {
            throw new IllegalArgumentException("La vigencia salarial ya está anulada.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar el motivo de anulación.");
        }
        Map<String, Object> anterior = resumen(registro);
        registro.setEstado(ANULADO);
        registro.setMotivo(registro.getMotivo() + " | ANULADO: " + motivo.trim());
        write.save(registro);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.UPDATE, "HISTORIAL_SALARIAL", registro.getId(),
                "Anulada vigencia salarial individual", anterior, resumen(registro));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialSalarioDto> listarPorTrabajador(UUID trabajadorId) {
        Trabajador trabajador = trabajadores.findById(trabajadorId)
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado."));
        validarLectura(trabajador.getFincaId());
        return read.findByTrabajadorIdOrderByFechaVigenciaDesc(trabajadorId).stream()
                .map(HistorialSalario::toAggregate).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HistorialSalarioDto vigente(UUID trabajadorId) {
        Trabajador trabajador = trabajadores.findById(trabajadorId)
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado."));
        validarLectura(trabajador.getFincaId());
        return read.findFirstByTrabajadorIdAndEstadoAndFechaVigenciaLessThanEqualOrderByFechaVigenciaDesc(
                trabajadorId, ACTIVO, LocalDate.now()).map(HistorialSalario::toAggregate).orElse(null);
    }

    private void validar(HistorialSalarioDto valor) {
        if (valor.getFechaVigencia() == null || valor.getSalarioEscala() == null
                || valor.getMotivo() == null || valor.getMotivo().trim().isEmpty()) {
            throw new IllegalArgumentException("Fecha de vigencia, salario de escala y motivo son obligatorios.");
        }
        if (valor.getSalarioEscala().compareTo(BigDecimal.ZERO) < 0
                || ceroSiNulo(valor.getAnticipoDiario()).compareTo(BigDecimal.ZERO) < 0
                || ceroSiNulo(valor.getTasa()).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Los importes salariales no pueden ser negativos.");
        }
    }

    private BigDecimal ceroSiNulo(BigDecimal importe) { return importe == null ? BigDecimal.ZERO : importe; }
    private void validarLectura(UUID fincaId) { if (fincaId != null && TenantContext.get() != null) TenantValidator.validateReadAccess(fincaId); }
    private void validarEscritura(UUID fincaId) { if (fincaId != null && TenantContext.get() != null) TenantValidator.validateWriteAccess(fincaId); }
    private Map<String, Object> resumen(HistorialSalario s) {
        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("trabajadorId", s.getTrabajadorId()); datos.put("fincaId", s.getFincaId());
        datos.put("cargoId", s.getCargoId()); datos.put("fechaVigencia", s.getFechaVigencia());
        datos.put("salarioEscala", s.getSalarioEscala()); datos.put("anticipoDiario", s.getAnticipoDiario());
        datos.put("tasa", s.getTasa()); datos.put("estado", s.getEstado()); return datos;
    }
}
