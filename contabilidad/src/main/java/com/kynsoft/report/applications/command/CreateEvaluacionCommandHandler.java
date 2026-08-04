package com.kynsoft.report.applications.command;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.applications.command.message.CreateEvaluacionMessage;
import com.kynsoft.report.domain.dto.EvaluacionDto;
import com.kynsoft.report.domain.services.IEvaluacionService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CreateEvaluacionCommandHandler implements ICommandHandler<CreateEvaluacionCommand> {

    private final IEvaluacionService serviceImpl;

    public CreateEvaluacionCommandHandler(IEvaluacionService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(CreateEvaluacionCommand command) {
        serviceImpl.create(EvaluacionDto
                .builder()
                .id(UUID.randomUUID())
                .trabajadorId(command.getTrabajadorId())
                .jefeId(command.getJefeId())
                .mes(command.getMes())
                .year(command.getYear())
                .calificacion(command.getCalificacion())
                .comentarios(command.getComentarios())
                .fechaEvaluacion(LocalDateTime.now())
                .build());
    }
}
