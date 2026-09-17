package com.kynsoft.report.applications.command.almacenproducto.entrada;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.time.LocalDate;

@Getter
@Setter
public class EntradaAlmacenCommand implements ICommand {
    private UUID almacenFincaProductoId;
    private Double cantidad;
    private TipoMovimientoStock tipo;
    private String descripcion;
    private String numeroFactura;
    private String numeroConduce;
    private String centroCosto;
    private Double stockNuevo;
    private String proveedor;
    private String responsableEntrega;
    private String responsableRecibe;
    private Double costoUnitario;
    private LocalDate fechaDocumento;
    private UUID informeRecepcionId;
    private UUID movimientoStockId;

    public EntradaAlmacenCommand(UUID almacenFincaProductoId, Double cantidad,
                                  TipoMovimientoStock tipo, String descripcion,
                                  String numeroFactura, String numeroConduce, String centroCosto) {
        this.almacenFincaProductoId = almacenFincaProductoId;
        this.cantidad = cantidad;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.numeroFactura = numeroFactura;
        this.numeroConduce = numeroConduce;
        this.centroCosto = centroCosto;
    }

    public static EntradaAlmacenCommand fromRequest(EntradaAlmacenRequest request) {
        EntradaAlmacenCommand command = new EntradaAlmacenCommand(
                request.getAlmacenFincaProductoId(),
                request.getCantidad(),
                request.getTipo(),
                request.getDescripcion(),
                request.getNumeroFactura(),
                request.getNumeroConduce(),
                request.getCentroCosto()
        );
        command.setProveedor(request.getProveedor());
        command.setResponsableEntrega(request.getResponsableEntrega());
        command.setResponsableRecibe(request.getResponsableRecibe());
        command.setCostoUnitario(request.getCostoUnitario());
        command.setFechaDocumento(request.getFechaDocumento());
        return command;
    }

    @Override
    public ICommandMessage getMessage() {
        return new EntradaAlmacenMessage(almacenFincaProductoId, cantidad, stockNuevo,
                informeRecepcionId, movimientoStockId);
    }
}
