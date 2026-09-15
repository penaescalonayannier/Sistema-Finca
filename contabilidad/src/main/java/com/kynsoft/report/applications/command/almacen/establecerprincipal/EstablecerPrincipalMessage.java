package com.kynsoft.report.applications.command.almacen.establecerprincipal;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class EstablecerPrincipalMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "ESTABLECER_PRINCIPAL";

    public EstablecerPrincipalMessage(UUID id) {
        this.id = id;
    }
}
