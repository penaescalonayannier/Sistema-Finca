package com.kynsoft.report.applications.query.responseObject;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.ClienteDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ClienteResponse implements IResponse {

    private UUID id;
    private String cuenta;
    private String nombre;
    private String ruc;
    private String direccion;

    public ClienteResponse(ClienteDto cliente) {
        this.id = cliente.getId();
        this.cuenta = cliente.getCuenta();
        this.nombre = cliente.getNombre();
        this.ruc = cliente.getRuc();
        this.direccion = cliente.getDireccion();
    }
}
