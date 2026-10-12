package com.kynsoft.report.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** Fotografía de una actividad agrícola prevista para el período. */
@Getter
@Setter
@Builder
public class ActividadPlanMensualDto {
    private UUID id;
    private Integer orden;
    private UUID tipoCultivoId;
    private String tipoCultivoNombreSnapshot;
    private String bloqueSnapshot;
    private String campoSnapshot;
    private UUID laborAgricolaId;
    private String laborAgricolaSnapshot;
    private String codigoCentroCosto;
    private String centroCostoSnapshot;
    private String actividad;
    private String unidadMedidaSnapshot;
    private BigDecimal areaPlanificada;
    private BigDecimal cantidadMeta;
    private BigDecimal pagoSalario;
    private BigDecimal compras;
    private BigDecimal servicios;
    private BigDecimal totalPlanificado;
    private List<DotacionPlanMensualDto> dotaciones;
    private List<GastoPlanMensualDto> gastos;
}
