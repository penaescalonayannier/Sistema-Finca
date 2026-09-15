package com.kynsoft.report.applications.command.trabajador.transferir;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TransferirTrabajadorCommand implements ICommand {
    private UUID trabajadorId;
    private UUID nuevaFincaId;
    private String fincaAnterior;
    private String fincaNueva;

    public static TransferirTrabajadorCommand fromRequest(TransferirTrabajadorRequest request, UUID trabajadorId) {
        return new TransferirTrabajadorCommand(trabajadorId, request.getNuevaFincaId(), null, null);
    }

    @Override
    public ICommandMessage getMessage() {
        return new TransferirTrabajadorMessage(trabajadorId, fincaAnterior, fincaNueva);
    }
}
