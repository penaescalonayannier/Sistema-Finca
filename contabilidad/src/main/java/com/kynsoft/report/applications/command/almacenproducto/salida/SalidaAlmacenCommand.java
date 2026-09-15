package com.kynsoft.report.applications.command.almacenproducto.salida;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SalidaAlmacenCommand implements ICommand {
    private UUID almacenFincaProductoId;
    private Integer cantidad;
    private String descripcion;
    private UUID trabajadorId;
    private String destino;
    private Double stockNuevo;

    public SalidaAlmacenCommand(UUID almacenFincaProductoId, Integer cantidad,
                                 String descripcion, UUID trabajadorId, String destino) {
        this.almacenFincaProductoId = almacenFincaProductoId;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.trabajadorId = trabajadorId;
        this.destino = destino;
    }

    public static SalidaAlmacenCommand fromRequest(SalidaAlmacenRequest request) {
        return new SalidaAlmacenCommand(
                request.getAlmacenFincaProductoId(),
                request.getCantidad(),
                request.getDescripcion(),
                request.getTrabajadorId(),
                request.getDestino()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new SalidaAlmacenMessage(almacenFincaProductoId, cantidad, stockNuevo);
    }
}
