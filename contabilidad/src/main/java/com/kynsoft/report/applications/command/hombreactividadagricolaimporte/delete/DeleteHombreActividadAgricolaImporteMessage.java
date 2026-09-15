package com.kynsoft.report.applications.command.hombreactividadagricolaimporte.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteHombreActividadAgricolaImporteMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_HOMBRE_ACTIVIDAD_AGRICOLA_IMPORTE";

    public DeleteHombreActividadAgricolaImporteMessage(UUID id) {
        this.id = id;
    }
}