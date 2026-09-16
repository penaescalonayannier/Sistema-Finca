package com.kynsoft.report.applications.command.almacenproducto.transferencia;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaAlmacenRequest {
    private UUID almacenFincaProductoId;
    private UUID destinoAlmacenId;
    private Double cantidad;
    private String observaciones;
}
