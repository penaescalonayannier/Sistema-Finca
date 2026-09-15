package com.kynsoft.report.applications.command.variedad.create;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateVariedadCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;

    public static CreateVariedadCommand fromRequest(CreateVariedadRequest request) {
        return new CreateVariedadCommand(
                UUID.randomUUID(),
                request.getCode(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateVariedadMessage(id);
    }
}