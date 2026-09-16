package com.kynsoft.report.applications.command.almacenproducto.produccionterminada;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class EntradaProduccionTerminadaAlmacenMessage implements ICommandMessage {
    private UUID produccionTerminadaId;
    private UUID almacenFincaProductoId;
    private Double stockNuevo;
    private final String command = "ENTRADA_PRODUCCION_TERMINADA_ALMACEN";
}
