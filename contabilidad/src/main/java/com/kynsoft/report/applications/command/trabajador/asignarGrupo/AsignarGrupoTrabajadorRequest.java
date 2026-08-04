package com.kynsoft.report.applications.command.trabajador.asignarGrupo;

import java.util.UUID;

public class AsignarGrupoTrabajadorRequest {
    private UUID trabajadorId;
    private UUID grupoId;

    public AsignarGrupoTrabajadorRequest() {}

    public AsignarGrupoTrabajadorRequest(UUID trabajadorId, UUID grupoId) {
        this.trabajadorId = trabajadorId;
        this.grupoId = grupoId;
    }

    public UUID getTrabajadorId() {
        return trabajadorId;
    }

    public void setTrabajadorId(UUID trabajadorId) {
        this.trabajadorId = trabajadorId;
    }

    public UUID getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(UUID grupoId) {
        this.grupoId = grupoId;
    }
}
