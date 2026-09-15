package com.kynsoft.report.applications.command.deudaTrabajador.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteDeudaTrabajadorMessage implements ICommandMessage {

    private UUID id;

    public DeleteDeudaTrabajadorMessage(UUID id) {
        this.id = id;
    }
}
