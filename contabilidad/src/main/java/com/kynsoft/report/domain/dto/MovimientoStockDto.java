package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MovimientoStockDto {
    private UUID id;
    private UUID fincaProductoId;
    private UUID fincaId;
    private UUID productoId;
    private UUID almacenId;
    private TipoMovimientoStock tipo;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private UUID referenciaId;
    private String referenciaTabla;
    private String descripcion;
    private String observaciones;
    private LocalDateTime fecha;
    private String centroCosto;  // Código del centro de costo para contabilidad

    // Campos adicionales para mostrar información relacionada
    private String fincaNombre;
    private String productoNombre;
    private String almacenNombre;
}
