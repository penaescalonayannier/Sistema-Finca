package com.kynsoft.report.applications.command.finca.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class DeleteFincaMessage implements ICommandMessage {
    private final UUID id;
    private final List<String> advertencias;
    private final String command = "DELETE_FINCA";

    public DeleteFincaMessage(UUID id, List<String> advertencias) {
        this.id = id;
        this.advertencias = advertencias;
    }
}