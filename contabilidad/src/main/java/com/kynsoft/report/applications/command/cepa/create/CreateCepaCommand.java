package com.kynsoft.report.applications.command.cepa.create;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateCepaCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;

    public static CreateCepaCommand fromRequest(CreateCepaRequest request) {
        return new CreateCepaCommand(
                UUID.randomUUID(),
                request.getCode(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateCepaMessage(id);
    }
}