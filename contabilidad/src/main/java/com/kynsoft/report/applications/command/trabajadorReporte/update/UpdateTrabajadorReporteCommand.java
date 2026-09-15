package com.kynsoft.report.applications.command.trabajadorReporte.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateTrabajadorReporteCommand implements ICommand {

    private UUID id;
    private String norma;
    private String horas;

    public static UpdateTrabajadorReporteCommand fromRequest(UpdateTrabajadorReporteRequest request, UUID id) {
        return new UpdateTrabajadorReporteCommand(
                id,
                request.getNorma(),
                request.getHoras()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateTrabajadorReporteMessage(id);
    }
}
