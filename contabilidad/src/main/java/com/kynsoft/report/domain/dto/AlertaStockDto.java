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
public class AlertaStockDto {
    private UUID fincaProductoId;
    private String fincaCode;
    private String fincaName;
    private String productoCode;
    private String productoName;
    private String unidadMedida;
    private Double stockActual;
    private Double stockMinimo;
    private Double deficit;
    private EstadoStock estado;
}
