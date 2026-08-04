package com.kynsoft.report.applications.command.report.estadoCuenta.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateEstadoCuentaMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_CLIENT";

    public CreateEstadoCuentaMessage(UUID id) {
        this.id = id;
    }

}
