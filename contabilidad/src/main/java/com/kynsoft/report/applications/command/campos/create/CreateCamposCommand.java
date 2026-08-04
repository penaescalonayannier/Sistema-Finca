package com.kynsoft.report.applications.command.campos.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class CreateCamposCommand implements ICommand {
    private UUID id;
    private UUID bloque;
    private UUID cepa;
    private UUID variedad;
    private String campo;
    private Double area;
    private Double poblacion;
    private String destino;
    private Double rendimiento;

    public static CreateCamposCommand fromRequest(CreateCamposRequest request) {
        return new CreateCamposCommand(
                UUID.randomUUID(),
                request.getBloque(),
                request.getCepa(),
                request.getVariedad(),
                request.getCampo(),
                request.getArea(),
                request.getPoblacion(),
                request.getDestino(),
                request.getRendimiento()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateCamposMessage(id);
    }
}