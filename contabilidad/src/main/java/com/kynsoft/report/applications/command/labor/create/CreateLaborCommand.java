package com.kynsoft.report.applications.command.labor.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateLaborCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;

    public static CreateLaborCommand fromRequest(CreateLaborRequest request) {
        return new CreateLaborCommand(
                UUID.randomUUID(),
                request.getCode(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateLaborMessage(id);
    }
}