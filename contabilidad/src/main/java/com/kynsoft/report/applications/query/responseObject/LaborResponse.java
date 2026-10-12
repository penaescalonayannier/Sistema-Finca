package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.LaborDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LaborResponse implements IResponse {
    private UUID id;
    private String code;
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

    public LaborResponse(LaborDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.activo = dto.getActivo();
        this.tipoCultivoId = dto.getTipoCultivoId();
        this.codigoCentroCosto = dto.getCodigoCentroCosto();
        this.unidadMedida = dto.getUnidadMedida();
        this.normaRendimiento = dto.getNormaRendimiento();
        this.indiceConsumo = dto.getIndiceConsumo();
        this.nivelActividad = dto.getNivelActividad();
        this.dotacionEstandar = dto.getDotacionEstandar();
        this.diasEstimados = dto.getDiasEstimados();
        this.horasEstimadas = dto.getHorasEstimadas();
        this.cargosRequeridos = dto.getCargosRequeridos();
    }
}
