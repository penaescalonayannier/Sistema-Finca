package com.kynsoft.report.applications.command.report.cuenta110.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateCuenta110Message implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_CLIENT";

    public CreateCuenta110Message(UUID id) {
        this.id = id;
    }

}
