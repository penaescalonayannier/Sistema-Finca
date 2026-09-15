package com.kynsoft.report.applications.command.fincaproducto.actualizar;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ActualizarStockFincaProductoCommand implements ICommand {
    private UUID fincaId;
    private UUID productoId;
    private Integer stock;

    public static ActualizarStockFincaProductoCommand fromRequest(ActualizarStockFincaProductoRequest request) {
        return new ActualizarStockFincaProductoCommand(
                request.getFincaId(),
                request.getProductoId(),
                request.getStock()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new ActualizarStockFincaProductoMessage(fincaId, productoId);
    }
}