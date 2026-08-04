package com.kynsoft.report.applications.command.trabajadorReporte.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateTrabajadorReporteMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_FINCA";

    public CreateTrabajadorReporteMessage(UUID id) {
        this.id = id;
    }
}