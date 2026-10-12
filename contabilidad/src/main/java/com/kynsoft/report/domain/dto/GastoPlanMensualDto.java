package com.kynsoft.report.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** Gasto planificado, persistido y atribuible a una actividad y centro de costo. */
@Getter
@Setter
@Builder
public class GastoPlanMensualDto {
    private UUID id;
    private Integer orden;
    private String categoria;
    private String subcategoria;
    private String descripcion;
    private String unidadMedidaSnapshot;
    private BigDecimal cantidad;
    private BigDecimal precioUnitarioSnapshot;
    private BigDecimal importePlanificado;
    private String observacion;
}
