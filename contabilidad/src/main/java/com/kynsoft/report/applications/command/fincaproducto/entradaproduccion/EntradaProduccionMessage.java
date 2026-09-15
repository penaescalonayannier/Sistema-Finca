package com.kynsoft.report.applications.command.fincaproducto.entradaproduccion;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class EntradaProduccionMessage implements ICommandMessage {
    private UUID fincaId;
    private UUID productoId;
    private Integer cantidadAgregada;
    private Double nuevoStock;
    private String mensaje;

    public EntradaProduccionMessage(UUID fincaId, UUID productoId, Integer cantidadAgregada) {
        this.fincaId = fincaId;
        this.productoId = productoId;
        this.cantidadAgregada = cantidadAgregada;
        this.nuevoStock = 0.0;
        this.mensaje = "Entrada registrada correctamente";
    }
}
