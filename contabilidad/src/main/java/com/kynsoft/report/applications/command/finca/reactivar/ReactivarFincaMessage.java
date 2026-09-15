package com.kynsoft.report.applications.command.finca.reactivar;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReactivarFincaMessage implements ICommandMessage {
    private UUID id;

    public static ReactivarFincaMessage fromCommand(ReactivarFincaCommand command) {
        return new ReactivarFincaMessage(command.getId());
    }
}
