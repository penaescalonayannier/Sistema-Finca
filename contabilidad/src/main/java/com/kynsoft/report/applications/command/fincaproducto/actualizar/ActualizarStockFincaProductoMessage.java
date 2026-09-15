package com.kynsoft.report.applications.command.fincaproducto.actualizar;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ActualizarStockFincaProductoMessage implements ICommandMessage {
    private final UUID fincaId;
    private final UUID productoId;
    private final String command = "ACTUALIZAR_STOCK_FINCA_PRODUCTO";

    public ActualizarStockFincaProductoMessage(UUID fincaId, UUID productoId) {
        this.fincaId = fincaId;
        this.productoId = productoId;
    }
}