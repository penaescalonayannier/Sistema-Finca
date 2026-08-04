package com.kynsoft.report.applications.command.reporte.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteReporteMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_FINCA";

    public DeleteReporteMessage(UUID id) {
        this.id = id;
    }
}