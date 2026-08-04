package com.kynsoft.report.applications.query.responseObject;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TrabajadoresFaltantesResponse implements IResponse {

    private String trabajadorId;
    private String nombre;
    private String ruc;
    private String cargo;
    private String cuenta;
}
