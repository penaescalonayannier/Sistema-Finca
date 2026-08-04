package com.kynsoft.report.applications.command.fincaproducto.asignar;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AsignarProductoAFincaMessage implements ICommandMessage {
    private final UUID fincaId;
    private final UUID productoId;
    private final String command = "ASIGNAR_PRODUCTO_A_FINCA";

    public AsignarProductoAFincaMessage(UUID fincaId, UUID productoId) {
        this.fincaId = fincaId;
        this.productoId = productoId;
    }
}