package com.kynsoft.report.applications.command.fincaproducto.remover;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class RemoverProductoDeFincaMessage implements ICommandMessage {
    private final UUID fincaId;
    private final UUID productoId;
    private final String command = "REMOVER_PRODUCTO_DE_FINCA";

    public RemoverProductoDeFincaMessage(UUID fincaId, UUID productoId) {
        this.fincaId = fincaId;
        this.productoId = productoId;
    }
}