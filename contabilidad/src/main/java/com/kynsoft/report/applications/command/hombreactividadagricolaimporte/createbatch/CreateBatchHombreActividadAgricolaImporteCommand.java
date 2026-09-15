package com.kynsoft.report.applications.command.hombreactividadagricolaimporte.createbatch;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateBatchHombreActividadAgricolaImporteCommand implements ICommand {
    private List<BatchHombreActividadAgricolaImporteItem> items;

    public static CreateBatchHombreActividadAgricolaImporteCommand fromRequest(CreateBatchHombreActividadAgricolaImporteRequest request) {
        return new CreateBatchHombreActividadAgricolaImporteCommand(request.getItems());
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateBatchHombreActividadAgricolaImporteMessage();
    }
}