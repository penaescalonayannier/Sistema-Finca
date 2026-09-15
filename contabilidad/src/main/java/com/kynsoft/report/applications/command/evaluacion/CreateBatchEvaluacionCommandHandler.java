package com.kynsoft.report.applications.command.evaluacion;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.EvaluacionDto;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.dto.FincaDto;
import com.kynsoft.report.domain.services.IEvaluacionService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import com.kynsoft.report.domain.services.IFincaService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class CreateBatchEvaluacionCommandHandler implements ICommandHandler<CreateBatchEvaluacionCommand> {

    private final IEvaluacionService evaluacionService;
    private final ITrabajadorService trabajadorService;
    private final IFincaService fincaService;

    public CreateBatchEvaluacionCommandHandler(
            IEvaluacionService evaluacionService,
            ITrabajadorService trabajadorService,
            IFincaService fincaService) {
        this.evaluacionService = evaluacionService;
        this.trabajadorService = trabajadorService;
        this.fincaService = fincaService;
    }

    @Override
    @Transactional
    public void handle(CreateBatchEvaluacionCommand command) {
        // Determinar el jefe a usar
        UUID jefeId = command.getJefeId();

        // Si no hay jefe, buscar uno por defecto
        if (jefeId == null && !command.getEvaluaciones().isEmpty()) {
            jefeId = obtenerJefePorDefecto(command.getEvaluaciones().get(0).getTrabajadorId());
        }

        for (CreateBatchEvaluacionRequest.CreateBatchEvaluacionItem item : command.getEvaluaciones()) {
            // Obtener grupo del trabajador si no está especificado
            UUID grupoId = command.getGrupoId();
            UUID jefeParaEsteItem = jefeId;

            if (grupoId == null || jefeParaEsteItem == null) {
                TrabajadorDto trabajador = trabajadorService.findById(item.getTrabajadorId());
                if (trabajador != null) {
                    if (grupoId == null) {
                        grupoId = trabajador.getGrupoId();
                    }
                    if (jefeParaEsteItem == null) {
                        jefeParaEsteItem = obtenerJefePorDefecto(item.getTrabajadorId());
                    }
                }
            }

            // Verificar si ya existe una evaluación para este trabajador/mes/año
            Optional<EvaluacionDto> existente = evaluacionService.findByTrabajadorAndMesAndYear(
                    item.getTrabajadorId(), command.getMes(), command.getYear());

            if (existente.isPresent()) {
                // Actualizar la evaluación existente
                EvaluacionDto evalExistente = existente.get();
                evalExistente.setCalificacion(item.getCalificacion());
                evalExistente.setComentarios(item.getComentarios());
                evalExistente.setFechaEvaluacion(LocalDateTime.now());
                evalExistente.setJefeId(jefeParaEsteItem);
                evalExistente.setGrupoId(grupoId);
                evaluacionService.update(evalExistente);
            } else {
                // Crear nueva evaluación
                EvaluacionDto evaluacionDto = EvaluacionDto.builder()
                        .id(UUID.randomUUID())
                        .grupoId(grupoId)
                        .trabajadorId(item.getTrabajadorId())
                        .jefeId(jefeParaEsteItem)
                        .mes(command.getMes())
                        .year(command.getYear())
                        .calificacion(item.getCalificacion())
                        .comentarios(item.getComentarios())
                        .fechaEvaluacion(LocalDateTime.now())
                        .build();

                evaluacionService.create(evaluacionDto);
            }
        }
    }

    /**
     * Obtiene un jefe por defecto para la evaluación.
     * Busca el responsable de la finca del trabajador.
     */
    private UUID obtenerJefePorDefecto(UUID trabajadorId) {
        try {
            TrabajadorDto trabajador = trabajadorService.findById(trabajadorId);
            if (trabajador != null && trabajador.getFincaId() != null) {
                FincaDto finca = fincaService.findById(trabajador.getFincaId());
                if (finca != null && finca.getResponsableId() != null) {
                    return finca.getResponsableId();
                }
            }
            // Si no hay responsable de finca, usar el mismo trabajador como auto-evaluación
            return trabajadorId;
        } catch (Exception e) {
            // En caso de error, usar el mismo trabajador
            return trabajadorId;
        }
    }
}
