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
public class AlmacenFincaProductoDto {
    private UUID id;
    private UUID almacenId;
    private String almacenNombre;
    private String almacenInventario;
    private UUID fincaProductoId;
    private UUID productoId;
    private String productoCode;
    private String productoName;
    private Double productoPrice;
    private UnidadMedida unidadMedida;
    private Integer stock;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private EstadoStock estadoStock;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Boolean getAlertaStockBajo() {
        return estadoStock == EstadoStock.CRITICO || estadoStock == EstadoStock.BAJO;
    }

    public Integer getDeficit() {
        if (stockMinimo == null || stockMinimo == 0 || stock == null) return 0;
        if (stock >= stockMinimo) return 0;
        return stockMinimo - stock;
    }
}
