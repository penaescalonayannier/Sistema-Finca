package com.kynsoft.report.applications.command.fincaproducto.asignar;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AsignarProductoAFincaRequest {
    private UUID fincaId;
    private UUID productoId;
    private Integer stock;
    private Integer stockMinimo;
}