package com.kynsoft.report.applications.command.almacenproducto.salida;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class SalidaAlmacenMessage implements ICommandMessage {
    private UUID almacenFincaProductoId;
    private Integer cantidad;
    private Integer stockNuevo;
    private final String command = "SALIDA_ALMACEN";
}
