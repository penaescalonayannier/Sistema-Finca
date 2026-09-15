package com.kynsoft.report.applications.command.trabajador.delete;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DeleteTrabajadorCommand implements ICommand {

    private UUID id;
    private List<String> advertencias = new ArrayList<>();

    public DeleteTrabajadorCommand(UUID id) {
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteTrabajadorMessage(id, advertencias);
    }
}