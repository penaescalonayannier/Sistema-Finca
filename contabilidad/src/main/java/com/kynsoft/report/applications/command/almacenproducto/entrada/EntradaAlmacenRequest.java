package com.kynsoft.report.applications.command.almacenproducto.entrada;

import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntradaAlmacenRequest {
    private UUID almacenFincaProductoId;
    private Double cantidad;
    private TipoMovimientoStock tipo;
    private String descripcion;
    private String numeroFactura;
    private String numeroConduce;
    private String centroCosto;  // Código del centro de costo (ej: 700.01.04 para Plátano)
    // Datos inmutables del informe de recepción SC-2-04.
    private String proveedor;
    private String responsableEntrega;
    private String responsableRecibe;
    private Double costoUnitario;
    private LocalDate fechaDocumento;
}
