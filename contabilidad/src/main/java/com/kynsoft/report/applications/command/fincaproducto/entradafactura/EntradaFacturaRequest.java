package com.kynsoft.report.applications.command.fincaproducto.entradafactura;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntradaFacturaRequest {
    private Integer cantidad;
    private String numeroFactura;
    private String observaciones;
}
