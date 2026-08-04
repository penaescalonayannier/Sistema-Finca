package com.kynsoft.report.applications.command.bloque.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateBloqueCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;

    public static UpdateBloqueCommand fromRequest(UpdateBloqueRequest request, UUID id) {
        return new UpdateBloqueCommand(
                id,
                request.getCode(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateBloqueMessage(id);
    }
}