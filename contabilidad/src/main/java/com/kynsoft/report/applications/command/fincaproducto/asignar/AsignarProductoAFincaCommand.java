package com.kynsoft.report.applications.command.fincaproducto.asignar;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class AsignarProductoAFincaCommand implements ICommand {
    private UUID fincaId;
    private UUID productoId;
    private Integer stock;

    public static AsignarProductoAFincaCommand fromRequest(AsignarProductoAFincaRequest request) {
        return new AsignarProductoAFincaCommand(
                request.getFincaId(),
                request.getProductoId(),
                request.getStock()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new AsignarProductoAFincaMessage(fincaId, productoId);
    }
}