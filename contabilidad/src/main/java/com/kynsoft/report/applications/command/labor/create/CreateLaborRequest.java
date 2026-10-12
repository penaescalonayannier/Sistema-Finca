package com.kynsoft.report.applications.command.labor.create;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateLaborRequest {
    @JsonAlias("codigo")
    private String code;
    @JsonAlias("nombre")
    private String name;
    private Boolean activo;
    private UUID tipoCultivoId;
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
