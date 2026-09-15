package com.kynsoft.report.applications.command.hombreactividadagricolaimporte.createbatch;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class CreateBatchHombreActividadAgricolaImporteMessage implements ICommandMessage {
    private final List<UUID> ids;
    private final String command = "CREATE_BATCH_HOMBRE_ACTIVIDAD_AGRICOLA_IMPORTE";

    public CreateBatchHombreActividadAgricolaImporteMessage(List<UUID> ids) {
        this.ids = ids;
    }

    public CreateBatchHombreActividadAgricolaImporteMessage() {
        this.ids = null;
    }
}