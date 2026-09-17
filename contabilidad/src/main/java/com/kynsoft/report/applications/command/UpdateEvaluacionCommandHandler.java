package com.kynsoft.report.applications.command;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.applications.command.message.UpdateEvaluacionMessage;
import com.kynsoft.report.domain.dto.EvaluacionDto;
import com.kynsoft.report.domain.services.IEvaluacionService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UpdateEvaluacionCommandHandler implements ICommandHandler<UpdateEvaluacionCommand> {

    private final IEvaluacionService serviceImpl;

    public UpdateEvaluacionCommandHandler(IEvaluacionService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(UpdateEvaluacionCommand command) {
        serviceImpl.update(EvaluacionDto
                .builder()
                .id(command.getId())
                .trabajadorId(command.getTrabajadorId())
                .jefeId(command.getJefeId())
                .mes(command.getMes())
                .year(command.getYear())
                .calificacion(command.getCalificacion())
                .comentarios(command.getComentarios())
                .evidencia(command.getEvidencia())
                .criteriosAplicados(command.getCriteriosAplicados())
                .constanciaJefe(command.getConstanciaJefe())
                .constanciaTrabajador(command.getConstanciaTrabajador())
                .fechaEvaluacion(LocalDateTime.now())
                .build());
    }
}
