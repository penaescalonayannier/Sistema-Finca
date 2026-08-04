package com.kynsoft.report.applications.command;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.applications.command.message.DeleteEvaluacionMessage;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteEvaluacionCommand implements ICommand {
    private UUID id;

    public DeleteEvaluacionCommand(UUID id) {
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteEvaluacionMessage(id);
    }
}
