package com.kynsoft.report.applications.command.report.cuenta110.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateCuenta110Message implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_CLIENT";

    public UpdateCuenta110Message(UUID id) {
        this.id = id;
    }

}
