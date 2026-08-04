package com.kynsoft.report.applications.command.finca.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateFincaCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;
    private String description;

    public static CreateFincaCommand fromRequest(CreateFincaRequest request) {
        return new CreateFincaCommand(
                UUID.randomUUID(),
                request.getCode(),
                request.getName(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateFincaMessage(id);
    }
}