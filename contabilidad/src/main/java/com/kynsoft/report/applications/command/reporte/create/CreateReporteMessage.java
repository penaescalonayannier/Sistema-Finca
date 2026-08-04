package com.kynsoft.report.applications.command.reporte.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateReporteMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_FINCA";

    public CreateReporteMessage(UUID id) {
        this.id = id;
    }
}