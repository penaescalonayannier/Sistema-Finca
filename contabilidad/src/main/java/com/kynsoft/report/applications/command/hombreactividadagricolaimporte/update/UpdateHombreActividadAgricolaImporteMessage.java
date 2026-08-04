package com.kynsoft.report.applications.command.hombreactividadagricolaimporte.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateHombreActividadAgricolaImporteMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_HOMBRE_ACTIVIDAD_AGRICOLA_IMPORTE";

    public UpdateHombreActividadAgricolaImporteMessage(UUID id) {
        this.id = id;
    }
}