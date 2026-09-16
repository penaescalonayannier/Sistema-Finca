package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class FincaProductoDto {
    private UUID id;
    private UUID fincaProductoId; // ID real del FincaProducto para operaciones de salida
    private UUID fincaId;
    private UUID productoId;
    private Double stock;
    private Double stockMinimo;
    private Double stockMaximo;
    private EstadoStock estadoStock;

    // Campos adicionales para mostrar información relacionada
    private String fincaCode;
    private String fincaName;
    private String productoCode;
    private String productoName;
    private Double productoPrice;
    private TipoProducto productoTipo;
    private Boolean activo;

    public static class FincaProductoDtoBuilder {
        public FincaProductoDtoBuilder stock(Number stock) {
            this.stock = stock != null ? stock.doubleValue() : null;
            return this;
        }

        public FincaProductoDtoBuilder stockMinimo(Number stockMinimo) {
            this.stockMinimo = stockMinimo != null ? stockMinimo.doubleValue() : null;
            return this;
        }

        public FincaProductoDtoBuilder stockMaximo(Number stockMaximo) {
            this.stockMaximo = stockMaximo != null ? stockMaximo.doubleValue() : null;
            return this;
        }
    }

    // Campo calculado (legacy, usar estadoStock)
    public Boolean getAlertaStockBajo() {
        return estadoStock == EstadoStock.CRITICO || estadoStock == EstadoStock.BAJO;
    }

    // Calcula déficit si hay alerta
    public Double getDeficit() {
        if (stockMinimo == null || stockMinimo == 0.0 || stock == null) return 0.0;
        if (stock >= stockMinimo) return 0.0;
        return stockMinimo - stock;
    }
}
