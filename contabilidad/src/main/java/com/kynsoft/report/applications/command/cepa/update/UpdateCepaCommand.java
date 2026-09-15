package com.kynsoft.report.applications.command.cepa.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateCepaCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;

    public static UpdateCepaCommand fromRequest(UpdateCepaRequest request, UUID id) {
        return new UpdateCepaCommand(
                id,
                request.getCode(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateCepaMessage(id);
    }
}