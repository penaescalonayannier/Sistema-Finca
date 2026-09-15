package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.EstadoCuentaDto;
import java.time.LocalDate;
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
public class EstadoCuentaResponse implements IResponse {

    private UUID id;
    private LocalDate fecha;
    private String refOrigen;
    private String refCorriente;
    private String observaciones;
    private String tipo;
    private Double importe;
    private UUID clienteId;

    public EstadoCuentaResponse(EstadoCuentaDto estadoCuenta) {
        this.id = estadoCuenta.getId();
        this.fecha = estadoCuenta.getFecha();
        this.refOrigen = estadoCuenta.getRefOrigen();
        this.refCorriente = estadoCuenta.getRefCorriente();
        this.observaciones = estadoCuenta.getObservaciones();
        this.tipo = estadoCuenta.getTipo();
        this.importe = estadoCuenta.getImporte();
        this.clienteId = estadoCuenta.getClienteId();
    }

}
