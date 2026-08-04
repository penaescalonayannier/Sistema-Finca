package com.kynsoft.report.applications.command.trabajador.asignarCargo;

import java.util.UUID;

public class AsignarCargoTrabajadorRequest {
    private UUID trabajadorId;
    private UUID cargoId;

    public AsignarCargoTrabajadorRequest() {}

    public AsignarCargoTrabajadorRequest(UUID trabajadorId, UUID cargoId) {
        this.trabajadorId = trabajadorId;
        this.cargoId = cargoId;
    }

    public UUID getTrabajadorId() {
        return trabajadorId;
    }

    public void setTrabajadorId(UUID trabajadorId) {
        this.trabajadorId = trabajadorId;
    }

    public UUID getCargoId() {
        return cargoId;
    }

    public void setCargoId(UUID cargoId) {
        this.cargoId = cargoId;
    }
}
