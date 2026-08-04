package com.kynsoft.report.applications.command.evaluacion;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.EvaluacionDto;
import com.kynsoft.report.domain.services.IEvaluacionService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CreateBatchEvaluacionCommandHandler implements ICommandHandler<CreateBatchEvaluacionCommand> {

    private final IEvaluacionService evaluacionService;

    public CreateBatchEvaluacionCommandHandler(IEvaluacionService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    @Override
    @Transactional
    public void handle(CreateBatchEvaluacionCommand command) {
        for (CreateBatchEvaluacionRequest.CreateBatchEvaluacionItem item : command.getEvaluaciones()) {
            EvaluacionDto evaluacionDto = EvaluacionDto.builder()
                    .id(UUID.randomUUID())
                    .grupoId(command.getGrupoId())
                    .trabajadorId(item.getTrabajadorId())
                    .jefeId(command.getJefeId())
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
