package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.CriterioEvaluacionDto;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.services.ICriterioEvaluacionService;
import com.kynsoft.report.infrastructure.entity.CriterioEvaluacion;
import com.kynsoft.report.infrastructure.repository.command.CriterioEvaluacionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.CriterioEvaluacionReadDataJPARepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CriterioEvaluacionServiceImpl implements ICriterioEvaluacionService {
    private final CriterioEvaluacionWriteDataJPARepository writeRepository;
    private final CriterioEvaluacionReadDataJPARepository readRepository;
    private final AuditoriaTransaccionalService auditoria;

    public CriterioEvaluacionServiceImpl(CriterioEvaluacionWriteDataJPARepository writeRepository,
            CriterioEvaluacionReadDataJPARepository readRepository, AuditoriaTransaccionalService auditoria) {
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
        this.auditoria = auditoria;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CriterioEvaluacionDto> listar(boolean incluirInactivos) {
        List<CriterioEvaluacion> criterios = incluirInactivos
                ? readRepository.findAllByOrderByOrdenAscNombreAsc()
                : readRepository.findByActivoTrueOrderByOrdenAscNombreAsc();
        return criterios.stream().map(CriterioEvaluacion::toAggregate).toList();
    }

    @Override
    public CriterioEvaluacionDto guardar(CriterioEvaluacionDto criterio) {
        if (criterio == null || criterio.getNombre() == null || criterio.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del criterio es obligatorio.");
        }
        boolean nuevo = criterio.getId() == null;
        if (nuevo) criterio.setId(UUID.randomUUID());
        CriterioEvaluacion guardado = writeRepository.save(new CriterioEvaluacion(criterio));
        auditoria.registrarDespuesDeConfirmar(nuevo ? TipoAccion.CREATE : TipoAccion.UPDATE,
                "CRITERIO_EVALUACION", guardado.getId(),
                (nuevo ? "Creado" : "Actualizado") + " criterio de evaluación " + guardado.getNombre(),
                null, guardado.toAggregate());
        return guardado.toAggregate();
    }

    @Override
    public void desactivar(UUID id) {
        CriterioEvaluacion criterio = readRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Criterio de evaluación no encontrado."));
        criterio.setActivo(false);
        writeRepository.save(criterio);
        auditoria.registrarDespuesDeConfirmar(TipoAccion.DELETE, "CRITERIO_EVALUACION", id,
                "Desactivado criterio de evaluación " + criterio.getNombre(), null, criterio.toAggregate());
    }
}
