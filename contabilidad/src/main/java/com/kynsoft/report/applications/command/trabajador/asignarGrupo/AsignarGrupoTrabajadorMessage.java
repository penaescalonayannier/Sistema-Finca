package com.kynsoft.report.applications.command.trabajador.asignarGrupo;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;

import java.util.UUID;

public class AsignarGrupoTrabajadorMessage implements ICommandMessage {
    private final UUID trabajadorId;

    public AsignarGrupoTrabajadorMessage(UUID trabajadorId) {
        this.trabajadorId = trabajadorId;
    }

    public UUID getTrabajadorId() {
        return trabajadorId;
    }
}
