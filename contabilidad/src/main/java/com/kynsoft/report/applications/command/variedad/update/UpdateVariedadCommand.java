package com.kynsoft.report.applications.command.variedad.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateVariedadCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;

    public static UpdateVariedadCommand fromRequest(UpdateVariedadRequest request, UUID id) {
        return new UpdateVariedadCommand(
                id,
                request.getCode(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateVariedadMessage(id);
    }
}