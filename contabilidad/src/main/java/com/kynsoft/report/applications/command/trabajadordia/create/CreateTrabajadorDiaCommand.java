package com.kynsoft.report.applications.command.trabajadordia.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateTrabajadorDiaCommand implements ICommand {
    private UUID id;
    private UUID diaTrabajoId;
    private UUID trabajadorId;
    private String horas;
    private String norma;

    public static CreateTrabajadorDiaCommand fromRequest(CreateTrabajadorDiaRequest request, UUID diaTrabajoId) {
        return new CreateTrabajadorDiaCommand(
                UUID.randomUUID(),
                diaTrabajoId,
                request.getTrabajadorId(),
                request.getHoras(),
                request.getNorma()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateTrabajadorDiaMessage(id);
    }
}