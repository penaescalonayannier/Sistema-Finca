package com.kynsoft.report.applications.command.fincaproducto.asignar;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AsignarProductoAFincaMessage implements ICommandMessage {
    private final UUID id;
    private final String mensaje;
    private final String command = "ASIGNAR_PRODUCTO_A_FINCA";

    public AsignarProductoAFincaMessage(UUID id) {
        this.id = id;
        this.mensaje = "Producto asignado correctamente";
    }
}