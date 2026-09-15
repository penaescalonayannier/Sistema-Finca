package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ResumenAlertasDto {
    private Integer totalProductos;
    private Integer productosCriticos;
    private Integer productosBajos;
    private Integer productosNormales;
    private Integer productosExceso;
    private List<AlertaStockDto> alertas;
}
