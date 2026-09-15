package com.kynsoft.report.applications.command.trabajador.reactivar;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ReactivarTrabajadorCommand implements ICommand {
    private UUID id;

    @Override
    public ICommandMessage getMessage() {
        return new ReactivarTrabajadorMessage(id);
    }
}
