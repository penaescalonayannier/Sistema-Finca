package com.kynsoft.report.applications.command.almacenproducto.transferencia;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class TransferenciaAlmacenMessage implements ICommandMessage {
    private UUID almacenOrigenId;
    private UUID almacenDestinoId;
    private Integer cantidad;
    private Integer stockOrigenNuevo;
    private Integer stockDestinoNuevo;
    private final String command = "TRANSFERENCIA_ALMACEN";
}
