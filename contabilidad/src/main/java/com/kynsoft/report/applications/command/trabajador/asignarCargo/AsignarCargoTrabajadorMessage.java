package com.kynsoft.report.applications.command.trabajador.asignarCargo;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import java.util.UUID;

public class AsignarCargoTrabajadorMessage implements ICommandMessage {
    private final UUID trabajadorId;

    public AsignarCargoTrabajadorMessage(UUID trabajadorId) {
        this.trabajadorId = trabajadorId;
    }

    public UUID getTrabajadorId() {
        return trabajadorId;
    }
}
