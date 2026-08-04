package com.kynsoft.report.applications.command.diatrabajo.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateDiaTrabajoCommand implements ICommand {
    private UUID id;
    private UUID reporteId;
    private LocalDate fecha;

    public static CreateDiaTrabajoCommand fromRequest(CreateDiaTrabajoRequest request, UUID reporteId) {
        return new CreateDiaTrabajoCommand(
                UUID.randomUUID(),
                reporteId,
                request.getFecha()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateDiaTrabajoMessage(id);
    }
}