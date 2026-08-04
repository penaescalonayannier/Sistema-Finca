package com.kynsoft.report.applications.command.finca.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateFincaCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;
    private String description;

    public static UpdateFincaCommand fromRequest(UpdateFincaRequest request, UUID id) {
        return new UpdateFincaCommand(
                id,
                request.getCode(),
                request.getName(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateFincaMessage(id);
    }
}