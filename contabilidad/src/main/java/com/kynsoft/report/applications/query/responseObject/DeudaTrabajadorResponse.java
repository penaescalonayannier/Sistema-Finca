package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class DeudaTrabajadorResponse implements IResponse {

    private UUID id;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private String trabajadorRuc;
    private Double importe;

    public DeudaTrabajadorResponse(DeudaTrabajadorDto dto) {
        this.id = dto.getId();
        this.trabajadorId = dto.getTrabajadorId();
        this.trabajadorNombre = dto.getTrabajadorNombre();
        this.trabajadorRuc = dto.getTrabajadorRuc();
        this.importe = dto.getImporte();
    }
}
