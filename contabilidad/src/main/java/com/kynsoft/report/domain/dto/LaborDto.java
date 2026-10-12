package com.kynsoft.report.domain.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class LaborDto {
    private UUID id;
    private String code;
    private String name;
    private Boolean activo;
    private UUID tipoCultivoId;
    /** Código de la cuenta contable marcada como centro de costo. */
    private String codigoCentroCosto;
    private String unidadMedida;
    private BigDecimal normaRendimiento;
    private BigDecimal indiceConsumo;
    private String nivelActividad;
    private BigDecimal dotacionEstandar;
    private BigDecimal diasEstimados;
    private BigDecimal horasEstimadas;
    private Set<UUID> cargosRequeridos;
}
