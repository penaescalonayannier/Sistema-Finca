package com.kynsoft.report.applications.command.evaluacion;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.applications.command.message.CreateBatchEvaluacionMessage;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateBatchEvaluacionCommand implements ICommand {
    private String mes;
    private Integer year;
    private UUID grupoId;
    private UUID jefeId;
    private String evidencia;
    private String criteriosAplicados;
    private String constanciaJefe;
    private List<CreateBatchEvaluacionRequest.CreateBatchEvaluacionItem> evaluaciones;

    public static CreateBatchEvaluacionCommand fromRequest(CreateBatchEvaluacionRequest request) {
        return new CreateBatchEvaluacionCommand(
                request.getMes(),
                request.getYear(),
                request.getGrupoId(),
                request.getJefeId(),
                request.getEvidencia(),
                request.getCriteriosAplicados(),
                request.getConstanciaJefe(),
                request.getEvaluaciones()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateBatchEvaluacionMessage(evaluaciones.size() + " evaluaciones creadas exitosamente");
    }
}
