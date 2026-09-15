package com.kynsoft.report.applications.command.almacenproducto.salida;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalidaAlmacenRequest {
    private UUID almacenFincaProductoId;
    private Integer cantidad;
    private String descripcion;
    private UUID trabajadorId;
    private String destino;
}
