package com.kynsoft.report.applications.command.hombreactividadagricolaimporte.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateHombreActividadAgricolaImporteMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_HOMBRE_ACTIVIDAD_AGRICOLA_IMPORTE";

    public CreateHombreActividadAgricolaImporteMessage(UUID id) {
        this.id = id;
    }
}