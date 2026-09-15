package com.kynsoft.report.applications.command.unidadmedida.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateUnidadMedidaCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;

    public static UpdateUnidadMedidaCommand fromRequest(UpdateUnidadMedidaRequest request, UUID id) {
        return new UpdateUnidadMedidaCommand(
                id,
                request.getCode(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateUnidadMedidaMessage(id);
    }
}