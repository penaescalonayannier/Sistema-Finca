package com.kynsoft.report.applications.command.almacenproducto.entrada;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EntradaAlmacenCommand implements ICommand {
    private UUID almacenFincaProductoId;
    private Integer cantidad;
    private TipoMovimientoStock tipo;
    private String descripcion;
    private String numeroFactura;
    private String centroCosto;
    private Integer stockNuevo;

    public EntradaAlmacenCommand(UUID almacenFincaProductoId, Integer cantidad,
                                  TipoMovimientoStock tipo, String descripcion,
                                  String numeroFactura, String centroCosto) {
        this.almacenFincaProductoId = almacenFincaProductoId;
        this.cantidad = cantidad;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.numeroFactura = numeroFactura;
        this.centroCosto = centroCosto;
    }

    public static EntradaAlmacenCommand fromRequest(EntradaAlmacenRequest request) {
        return new EntradaAlmacenCommand(
                request.getAlmacenFincaProductoId(),
                request.getCantidad(),
                request.getTipo(),
                request.getDescripcion(),
                request.getNumeroFactura(),
                request.getCentroCosto()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new EntradaAlmacenMessage(almacenFincaProductoId, cantidad, stockNuevo);
    }
}
