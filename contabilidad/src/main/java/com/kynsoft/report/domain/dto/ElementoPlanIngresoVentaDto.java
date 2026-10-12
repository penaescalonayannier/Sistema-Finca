package com.kynsoft.report.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ElementoPlanIngresoVentaDto {
    private UUID id;
    private Integer orden;
    private String productoCodigoSnapshot;
    private String productoNombreSnapshot;
    private String unidadMedidaSnapshot;
    private String tipoPrecioSnapshot;
    private BigDecimal cantidadPlanificada;
    private BigDecimal precioUnitarioSnapshot;
    private BigDecimal importePlanificado;
}
