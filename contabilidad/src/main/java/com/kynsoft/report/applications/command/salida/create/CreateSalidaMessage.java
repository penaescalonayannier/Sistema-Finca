package com.kynsoft.report.applications.command.salida.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateSalidaMessage implements ICommandMessage {
    private UUID id;

    public static CreateSalidaMessage fromRequest(UUID id) {
        return new CreateSalidaMessage(id);
    }
}
