package com.kynsoft.report.applications.query.responseObject;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.BloqueDto;
import com.kynsoft.report.domain.dto.CampoDto;
import com.kynsoft.report.domain.dto.CepaDto;
import com.kynsoft.report.domain.dto.VariedadDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import lombok.Builder;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class CamposResponse implements IResponse {

    private UUID id;
    private BloqueDto bloque;
    private String campo;
    private Double area;
    private VariedadDto variedad;
    private CepaDto cepa;
    private Double poblacion;
    private String destino;
    private Double rendimiento;

    public CamposResponse(CampoDto dto) {
        this.id = dto.getId();
        this.bloque = dto.getBloque();
        this.campo = dto.getCampo();
        this.area = dto.getArea();
        this.variedad = dto.getVariedad();
        this.cepa = dto.getCepa();
        this.poblacion = dto.getPoblacion();
        this.destino = dto.getDestino();
        this.rendimiento = dto.getRendimiento();
    }

}
