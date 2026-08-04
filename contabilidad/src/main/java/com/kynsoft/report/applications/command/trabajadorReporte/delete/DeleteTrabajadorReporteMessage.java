package com.kynsoft.report.applications.command.trabajadorReporte.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteTrabajadorReporteMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_UNIDAD_MEDIDA";

    public DeleteTrabajadorReporteMessage(UUID id) {
        this.id = id;
    }
}