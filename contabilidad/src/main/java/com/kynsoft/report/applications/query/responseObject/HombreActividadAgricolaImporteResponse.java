package com.kynsoft.report.applications.query.responseObject;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.BloqueDto;
import com.kynsoft.report.domain.dto.CampoDto;
import com.kynsoft.report.domain.dto.HombreActividadAgricolaImporteDto;
import com.kynsoft.report.domain.dto.InstrumentoTrabajoDto;
import com.kynsoft.report.domain.dto.LaborDto;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HombreActividadAgricolaImporteResponse implements IResponse {
    private UUID id;
    private TrabajadorDto trabajador;
    private LaborDto labor;
    private InstrumentoTrabajoDto instrumento;
    private BloqueDto bloque;
    private CampoDto campo;
    private BigDecimal dias;
    private BigDecimal horas;
    private BigDecimal norma;
    private BigDecimal tasa;
    private BigDecimal importe;

    public HombreActividadAgricolaImporteResponse(HombreActividadAgricolaImporteDto dto) {
        this.id = dto.getId();
        this.trabajador = dto.getTrabajador();
        this.labor = dto.getLabor();
        this.instrumento = dto.getInstrumento();
        this.bloque = dto.getBloque();
        this.campo = dto.getCampo();
        this.dias = dto.getDias();
        this.horas = dto.getHoras();
        this.norma = dto.getNorma();
        this.tasa = dto.getTasa();
        this.importe = dto.getImporte();
    }
}