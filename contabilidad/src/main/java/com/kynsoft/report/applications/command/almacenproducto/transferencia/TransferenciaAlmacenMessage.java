package com.kynsoft.report.applications.command.almacenproducto.transferencia;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class TransferenciaAlmacenMessage implements ICommandMessage {
    private UUID transferenciaId;
    private UUID almacenOrigenId;
    private UUID almacenDestinoId;
    private Double cantidad;
    private Double stockOrigenNuevo;
    private Double stockDestinoNuevo;
    private final String command = "TRANSFERENCIA_ALMACEN";
}
