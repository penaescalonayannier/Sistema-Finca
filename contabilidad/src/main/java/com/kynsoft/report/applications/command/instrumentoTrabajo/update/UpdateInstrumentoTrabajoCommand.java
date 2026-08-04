package com.kynsoft.report.applications.command.instrumentoTrabajo.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateInstrumentoTrabajoCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;

    public static UpdateInstrumentoTrabajoCommand fromRequest(UpdateInstrumentoTrabajoRequest request, UUID id) {
        return new UpdateInstrumentoTrabajoCommand(
                id,
                request.getCode(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateInstrumentoTrabajoMessage(id);
    }
}