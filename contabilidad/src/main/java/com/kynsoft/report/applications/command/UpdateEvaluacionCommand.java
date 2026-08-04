package com.kynsoft.report.applications.command;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.applications.command.message.UpdateEvaluacionMessage;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEvaluacionCommand implements ICommand {
    private UUID id;
    private UUID trabajadorId;
    private UUID jefeId;
    private String mes;
    private Integer year;
    private Integer calificacion;
    private String comentarios;

    public UpdateEvaluacionCommand(
            UUID id,
            UUID trabajadorId,
            UUID jefeId,
            String mes,
            Integer year,
            Integer calificacion,
            String comentarios) {
        this.id = id;
        this.trabajadorId = trabajadorId;
        this.jefeId = jefeId;
        this.mes = mes;
        this.year = year;
        this.calificacion = calificacion;
        this.comentarios = comentarios;
    }

    public static UpdateEvaluacionCommand fromRequest(UpdateEvaluacionRequest request) {
        return new UpdateEvaluacionCommand(
                request.id,
                request.trabajadorId,
                request.jefeId,
                request.mes,
                request.year,
                request.calificacion,
                request.comentarios
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateEvaluacionMessage(id, trabajadorId, jefeId, mes, year, calificacion, comentarios, null);
    }

    public static class UpdateEvaluacionRequest {
        public UUID id;
        public UUID trabajadorId;
        public UUID jefeId;
        public String mes;
        public Integer year;
        public Integer calificacion;
        public String comentarios;

        public void setId(UUID id) {
            this.id = id;
        }
    }
}
