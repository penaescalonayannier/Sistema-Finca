package com.kynsoft.report.applications.command.fincaproducto.actualizar;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ActualizarStockFincaProductoRequest {
    private UUID fincaId;
    private UUID productoId;
    private Double stock;
}
