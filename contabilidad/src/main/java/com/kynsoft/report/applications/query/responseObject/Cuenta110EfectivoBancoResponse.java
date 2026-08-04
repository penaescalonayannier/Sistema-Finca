package com.kynsoft.report.applications.query.responseObject;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.Cuenta110EfectivoBancoDto;
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
public class Cuenta110EfectivoBancoResponse implements IResponse {
    private UUID id;
    private String observaciones;
    private Double importe;

    public Cuenta110EfectivoBancoResponse(Cuenta110EfectivoBancoDto cuenta110EfectivoBanco) {
        this.id = cuenta110EfectivoBanco.getId();
        this.observaciones = cuenta110EfectivoBanco.getObservaciones();
        this.importe = cuenta110EfectivoBanco.getImporte();
    }

}
