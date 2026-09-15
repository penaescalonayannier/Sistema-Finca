package com.kynsoft.report.applications.command.trabajador.asignarCargo;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AsignarCargoTrabajadorCommand implements ICommand {

    private UUID trabajadorId;
    private UUID cargoId;

    public AsignarCargoTrabajadorCommand(UUID trabajadorId, UUID cargoId) {
        this.trabajadorId = trabajadorId;
        this.cargoId = cargoId;
    }

    public static AsignarCargoTrabajadorCommand fromRequest(AsignarCargoTrabajadorRequest request) {
        return new AsignarCargoTrabajadorCommand(
                request.getTrabajadorId(),
                request.getCargoId()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new AsignarCargoTrabajadorMessage(trabajadorId);
    }
}
