package com.kynsoft.report.applications.command.bloque.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateBloqueCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;

    public static CreateBloqueCommand fromRequest(CreateBloqueRequest request) {
        return new CreateBloqueCommand(
                UUID.randomUUID(),
                request.getCode(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateBloqueMessage(id);
    }
}