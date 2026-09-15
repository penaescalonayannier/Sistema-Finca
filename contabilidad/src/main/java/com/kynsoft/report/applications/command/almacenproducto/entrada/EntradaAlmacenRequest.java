package com.kynsoft.report.applications.command.almacenproducto.entrada;

import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntradaAlmacenRequest {
    private UUID almacenFincaProductoId;
    private Integer cantidad;
    private TipoMovimientoStock tipo;
    private String descripcion;
    private String numeroFactura;
    private String centroCosto;  // Código del centro de costo (ej: 700.01.04 para Plátano)
}
