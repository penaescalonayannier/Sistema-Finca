package com.kynsoft.report.applications.command.fincaproducto.entradaproduccion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntradaProduccionRequest {
    private UUID fincaId;
    private UUID productoId;
    private Integer cantidad;
    private String descripcion;
    private String centroCosto;  // Código del centro de costo para contabilidad (ej: 700.01.04)
}
