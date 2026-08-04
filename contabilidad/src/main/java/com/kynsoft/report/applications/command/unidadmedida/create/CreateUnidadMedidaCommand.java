package com.kynsoft.report.applications.command.unidadmedida.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateUnidadMedidaCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;

    public static CreateUnidadMedidaCommand fromRequest(CreateUnidadMedidaRequest request) {
        return new CreateUnidadMedidaCommand(
                UUID.randomUUID(),
                request.getCode(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateUnidadMedidaMessage(id);
    }
}