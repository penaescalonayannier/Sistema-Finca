package com.kynsoft.report.applications.command.deudaTrabajador.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateDeudaTrabajadorMessage implements ICommandMessage {

    private UUID id;

    public UpdateDeudaTrabajadorMessage(UUID id) {
        this.id = id;
    }
}
