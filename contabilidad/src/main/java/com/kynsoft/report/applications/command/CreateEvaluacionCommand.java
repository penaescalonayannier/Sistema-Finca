package com.kynsoft.report.applications.command;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.applications.command.message.CreateEvaluacionMessage;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEvaluacionCommand implements ICommand {
    private UUID id;
    private UUID trabajadorId;
    private UUID jefeId;
    private String mes;
    private Integer year;
    private Integer calificacion;
    private String comentarios;
    private String evidencia;
    private String criteriosAplicados;
    private String constanciaJefe;
    private String constanciaTrabajador;

    public CreateEvaluacionCommand(
            UUID trabajadorId,
            UUID jefeId,
            String mes,
            Integer year,
            Integer calificacion,
            String comentarios) {
        this.id = UUID.randomUUID();
        this.trabajadorId = trabajadorId;
        this.jefeId = jefeId;
        this.mes = mes;
        this.year = year;
        this.calificacion = calificacion;
        this.comentarios = comentarios;
    }

    public static CreateEvaluacionCommand fromRequest(CreateEvaluacionRequest request) {
        CreateEvaluacionCommand command = new CreateEvaluacionCommand(
                request.trabajadorId,
                request.jefeId,
                request.mes,
                request.year,
                request.calificacion,
                request.comentarios
        );
        command.setEvidencia(request.evidencia);
        command.setCriteriosAplicados(request.criteriosAplicados);
        command.setConstanciaJefe(request.constanciaJefe);
        command.setConstanciaTrabajador(request.constanciaTrabajador);
        return command;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateEvaluacionMessage(id, trabajadorId, jefeId, mes, year, calificacion, comentarios, null);
    }

    public static class CreateEvaluacionRequest {
        public UUID trabajadorId;
        public UUID jefeId;
        public String mes;
        public Integer year;
        public Integer calificacion;
        public String comentarios;
        public String evidencia;
        public String criteriosAplicados;
        public String constanciaJefe;
        public String constanciaTrabajador;
    }
}
