package com.kynsoft.report.applications.command.labor.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateLaborCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;

    public static UpdateLaborCommand fromRequest(UpdateLaborRequest request, UUID id) {
        return new UpdateLaborCommand(
                id,
                request.getCode(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateLaborMessage(id);
    }
}