package com.kynsoft.report.applications.command.hombreactividadagricolaimporte.delete;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteHombreActividadAgricolaImporteCommand implements ICommand {
    private UUID id;

    public DeleteHombreActividadAgricolaImporteCommand(UUID id) {
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteHombreActividadAgricolaImporteMessage(id);
    }
}