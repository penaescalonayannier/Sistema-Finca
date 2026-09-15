package com.kynsoft.report.applications.command.reporte.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateReporteMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_FINCA";

    public UpdateReporteMessage(UUID id) {
        this.id = id;
    }
}