package com.kynsoft.report.applications.command.almacenproducto.entrada;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class EntradaAlmacenMessage implements ICommandMessage {
    private UUID almacenFincaProductoId;
    private Double cantidad;
    private Double stockNuevo;
    private final String command = "ENTRADA_ALMACEN";
}
