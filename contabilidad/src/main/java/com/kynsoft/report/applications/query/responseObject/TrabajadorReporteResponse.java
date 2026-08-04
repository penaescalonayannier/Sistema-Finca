package com.kynsoft.report.applications.query.responseObject;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.TrabajadorReporteDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import lombok.Builder;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TrabajadorReporteResponse implements IResponse {

    private UUID id;
    private UUID trabajador;
    private UUID reporte;
    private String norma;
    private String horas;

    public TrabajadorReporteResponse(TrabajadorReporteDto dto) {
        this.id = dto.getId();
        this.trabajador = dto.getTrabajador();
        this.reporte = dto.getReporte();
        this.norma = dto.getNorma();
        this.horas = dto.getHoras();
    }

}
