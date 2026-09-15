package com.kynsoft.report.applications.command.almacen.establecerprincipal;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class EstablecerPrincipalCommand implements ICommand {
    private UUID id;

    @Override
    public ICommandMessage getMessage() {
        return new EstablecerPrincipalMessage(id);
    }
}
