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
    private UUID fincaId;
    private UUID productoId;
    private Integer stock;
    
    // Campos adicionales para mostrar información relacionada
    private String fincaCode;
    private String fincaName;
    private String productoCode;
    private String productoName;
    private Double productoPrice;
}