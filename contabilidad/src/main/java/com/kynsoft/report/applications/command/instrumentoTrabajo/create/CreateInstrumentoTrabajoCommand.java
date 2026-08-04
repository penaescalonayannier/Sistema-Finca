package com.kynsoft.report.applications.command.instrumentoTrabajo.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateInstrumentoTrabajoCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;

    public static CreateInstrumentoTrabajoCommand fromRequest(CreateInstrumentoTrabajoRequest request) {
        return new CreateInstrumentoTrabajoCommand(
                UUID.randomUUID(),
                request.getCode(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateInstrumentoTrabajoMessage(id);
    }
}