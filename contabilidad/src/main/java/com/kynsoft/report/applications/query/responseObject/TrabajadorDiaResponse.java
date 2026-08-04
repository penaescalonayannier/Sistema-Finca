package com.kynsoft.report.applications.query.responseObject;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.TrabajadorDiaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrabajadorDiaResponse implements IResponse {
    private UUID id;
    private UUID diaTrabajoId;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private String trabajadorRuc;
    private String horas;
    private String norma;

    public TrabajadorDiaResponse(TrabajadorDiaDto dto) {
        this.id = dto.getId();
        this.diaTrabajoId = dto.getDiaTrabajoId();
        this.trabajadorId = dto.getTrabajadorId();
        this.trabajadorNombre = dto.getTrabajadorNombre();
        this.trabajadorRuc = dto.getTrabajadorRuc();
        this.horas = dto.getHoras();
        this.norma = dto.getNorma();
    }
}