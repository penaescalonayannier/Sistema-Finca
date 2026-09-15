package com.kynsoft.report.applications.command.deudaTrabajador.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateDeudaTrabajadorMessage implements ICommandMessage {

    private UUID id;

    public CreateDeudaTrabajadorMessage(UUID id) {
        this.id = id;
    }
}
