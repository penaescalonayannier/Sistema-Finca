package com.kynsoft.report.applications.command.trabajadorReporte.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateTrabajadorReporteCommand implements ICommand {

    private UUID id;
    private UUID trabajador;
    private UUID reporte;
    private String norma;
    private String horas;

    public static CreateTrabajadorReporteCommand fromRequest(CreateTrabajadorReporteRequest request) {
        return new CreateTrabajadorReporteCommand(
                UUID.randomUUID(),
                request.getTrabajador(),
                request.getReporte(),
                request.getNorma(),
                request.getHoras()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateTrabajadorReporteMessage(id);
    }
}
