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
    private Integer stock;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private EstadoStock estadoStock;

    // Campos adicionales para mostrar información relacionada
    private String fincaCode;
    private String fincaName;
    private String productoCode;
    private String productoName;
    private Double productoPrice;
    private TipoProducto productoTipo;
    private Boolean activo;

    // Campo calculado (legacy, usar estadoStock)
    public Boolean getAlertaStockBajo() {
        return estadoStock == EstadoStock.CRITICO || estadoStock == EstadoStock.BAJO;
    }

    // Calcula déficit si hay alerta
    public Integer getDeficit() {
        if (stockMinimo == null || stockMinimo == 0 || stock == null) return 0;
        if (stock >= stockMinimo) return 0;
        return stockMinimo - stock;
    }
}