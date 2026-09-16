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
    private Double cantidad;
    private Double stockAnterior;
    private Double stockNuevo;
    private UUID referenciaId;
    private String referenciaTabla;
    private String descripcion;
    private String observaciones;
    private LocalDateTime fecha;
    private String centroCosto;  // Código del centro de costo para contabilidad

    public static class MovimientoStockDtoBuilder {
        public MovimientoStockDtoBuilder cantidad(Number cantidad) {
            this.cantidad = cantidad != null ? cantidad.doubleValue() : null;
            return this;
        }

        public MovimientoStockDtoBuilder stockAnterior(Number stockAnterior) {
            this.stockAnterior = stockAnterior != null ? stockAnterior.doubleValue() : null;
            return this;
        }

        public MovimientoStockDtoBuilder stockNuevo(Number stockNuevo) {
            this.stockNuevo = stockNuevo != null ? stockNuevo.doubleValue() : null;
            return this;
        }
    }

    // Campos adicionales para mostrar información relacionada
    private String fincaNombre;
    private String productoNombre;
    private String almacenNombre;
}
