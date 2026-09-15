package com.kynsoft.report.applications.command.trabajadorReporte.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateTrabajadorReporteMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_REPORTE_TRABAJADOR";

    public UpdateTrabajadorReporteMessage(UUID id) {
        this.id = id;
    }
}