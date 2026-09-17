package com.kynsoft.report.infrastructure.services;

import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.EvaluacionResponse;
import com.kynsoft.report.domain.dto.EvaluacionDto;
import com.kynsoft.report.domain.dto.EstadoEvaluacion;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.services.IEvaluacionService;
import com.kynsoft.report.infrastructure.entity.Evaluacion;
import com.kynsoft.report.infrastructure.repository.command.EvaluacionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.EvaluacionReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class EvaluacionServiceImpl implements IEvaluacionService {

    private final EvaluacionWriteDataJPARepository repositoryCommand;
    private final EvaluacionReadDataJPARepository repositoryQuery;
    private final AuditoriaTransaccionalService auditoria;

    public EvaluacionServiceImpl(EvaluacionWriteDataJPARepository repositoryCommand,
                                EvaluacionReadDataJPARepository repositoryQuery,
                                AuditoriaTransaccionalService auditoria) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.auditoria = auditoria;
    }

    @Override
    public void create(EvaluacionDto object) {
        if (object.getEstado() == null) object.setEstado(EstadoEvaluacion.BORRADOR);
        Evaluacion creada = repositoryCommand.save(new Evaluacion(object));
        auditoria.registrarDespuesDeConfirmar(TipoAccion.CREATE, "EVALUACION_DESEMPENO", creada.getId(),
                "Creada evaluación de desempeño", null, creada.toAggregate());
    }

    @Override
    public void update(EvaluacionDto object) {
        Evaluacion existente = obtener(object.getId());
        asegurarEditable(existente);
        EvaluacionDto anterior = existente.toAggregate();
        existente.setGrupoId(object.getGrupoId());
        existente.setTrabajadorId(object.getTrabajadorId());
        existente.setJefeId(object.getJefeId());
        existente.setMes(object.getMes());
        existente.setYear(object.getYear());
        existente.setCalificacion(object.getCalificacion());
        existente.setComentarios(object.getComentarios());
        existente.setFechaEvaluacion(object.getFechaEvaluacion());
        if (object.getEvidencia() != null) existente.setEvidencia(object.getEvidencia());
        if (object.getCriteriosAplicados() != null) existente.setCriteriosAplicados(object.getCriteriosAplicados());
        if (object.getConstanciaJefe() != null) existente.setConstanciaJefe(object.getConstanciaJefe());
        if (object.getConstanciaTrabajador() != null) existente.setConstanciaTrabajador(object.getConstanciaTrabajador());
        Evaluacion actualizada = repositoryCommand.save(existente);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.UPDATE, "EVALUACION_DESEMPENO", actualizada.getId(),
                "Actualizada evaluación de desempeño", anterior, actualizada.toAggregate());
    }

    @Override
    public void delete(UUID id) {
        Evaluacion existente = obtener(id);
        asegurarEditable(existente);
        EvaluacionDto anterior = existente.toAggregate();
        repositoryCommand.delete(existente);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.DELETE, "EVALUACION_DESEMPENO", id,
                "Eliminada evaluación en borrador", anterior, null);
    }

    @Override
    public EvaluacionDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Evaluacion::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "Evaluación not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Evaluacion> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Evaluacion> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Evaluacion> data) {
        List<EvaluacionResponse> responses = data.getContent().stream()
                .map(Evaluacion::toAggregate)
                .map(EvaluacionResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public List<EvaluacionDto> findByMesAndYear(String mes, Integer year) {
        return repositoryQuery.findByMesAndYear(mes, year).stream()
                .map(Evaluacion::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<EvaluacionDto> findByYearAndMeses(Integer year, List<String> meses) {
        return repositoryQuery.findByYearAndMesIn(year, meses).stream()
                .map(Evaluacion::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findDistinctMesesByYear(Integer year) {
        return repositoryQuery.findDistinctMesesByYear(year);
    }

    @Override
    public List<Integer> findDistinctYears() {
        return repositoryQuery.findDistinctYears();
    }

    @Override
    public Optional<EvaluacionDto> findByTrabajadorAndMesAndYear(UUID trabajadorId, String mes, Integer year) {
        return repositoryQuery.findByTrabajadorIdAndMesAndYear(trabajadorId, mes, year)
                .map(Evaluacion::toAggregate);
    }

    @Override
    public EvaluacionDto cambiarEstado(UUID id, EstadoEvaluacion estado, String constanciaJefe,
            String constanciaTrabajador, String observacionesCierre) {
        if (estado == null) throw new IllegalArgumentException("El estado de la evaluación es obligatorio.");
        Evaluacion evaluacion = obtener(id);
        EstadoEvaluacion anteriorEstado = evaluacion.getEstado() == null ? EstadoEvaluacion.BORRADOR : evaluacion.getEstado();
        validarTransicion(anteriorEstado, estado);
        EvaluacionDto anterior = evaluacion.toAggregate();
        evaluacion.setEstado(estado);
        if (constanciaJefe != null) evaluacion.setConstanciaJefe(constanciaJefe);
        if (constanciaTrabajador != null) evaluacion.setConstanciaTrabajador(constanciaTrabajador);
        if (estado == EstadoEvaluacion.ENVIADA) evaluacion.setFechaEnvio(java.time.LocalDateTime.now());
        if (estado == EstadoEvaluacion.CERRADA) {
            if (vacio(evaluacion.getConstanciaJefe()) || vacio(evaluacion.getConstanciaTrabajador())) {
                throw new IllegalArgumentException("Para cerrar la evaluación se requiere la constancia textual del jefe y del trabajador.");
            }
            evaluacion.setFechaCierre(java.time.LocalDateTime.now());
            evaluacion.setObservacionesCierre(observacionesCierre);
        }
        Evaluacion guardada = repositoryCommand.save(evaluacion);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.UPDATE, "EVALUACION_DESEMPENO", id,
                "Cambio de estado " + anteriorEstado + " a " + estado, anterior, guardada.toAggregate());
        return guardada.toAggregate();
    }

    private Evaluacion obtener(UUID id) {
        return repositoryQuery.findById(id).orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND, new ErrorField("id", "Evaluación not found."))));
    }

    private void asegurarEditable(Evaluacion evaluacion) {
        if (evaluacion.getEstado() == EstadoEvaluacion.CERRADA || evaluacion.getEstado() == EstadoEvaluacion.ANULADA) {
            throw new IllegalStateException("La evaluación cerrada o anulada es inmutable. Debe conservarse como evidencia documental.");
        }
    }

    private void validarTransicion(EstadoEvaluacion actual, EstadoEvaluacion destino) {
        if (actual == EstadoEvaluacion.CERRADA) {
            throw new IllegalStateException("Una evaluación cerrada no puede cambiar de estado.");
        }
        boolean valida = actual == destino
                || (actual == EstadoEvaluacion.BORRADOR && (destino == EstadoEvaluacion.ENVIADA || destino == EstadoEvaluacion.ANULADA))
                || (actual == EstadoEvaluacion.ENVIADA && (destino == EstadoEvaluacion.CERRADA || destino == EstadoEvaluacion.ANULADA));
        if (!valida) throw new IllegalArgumentException("Transición de estado de evaluación no permitida.");
    }

    private boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
