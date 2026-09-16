package com.kynsoft.report.applications.command.almacenproducto.produccionterminada;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntradaProduccionTerminadaAlmacenRequest {
    private UUID almacenFincaProductoId;
    private Double cantidadTerminada;
    private UUID trabajadorEntregaId;
    private UUID trabajadorRecibeId;
    private String observaciones;
    private Double costoUnitario;
    private String lote;
    private String centroCosto;
}
