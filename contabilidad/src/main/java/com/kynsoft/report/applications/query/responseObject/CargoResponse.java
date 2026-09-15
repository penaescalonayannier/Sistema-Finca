package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.CargoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class CargoResponse implements IResponse {
    private UUID id;
    private String name;
    private String description;
    private String tipoCargo;
    private BigDecimal anticipoDiario;
    private BigDecimal salarioEscala;
    private BigDecimal taza;

    public CargoResponse(CargoDto cargo) {
        this.id = cargo.getId();
        this.name = cargo.getName();
        this.description = cargo.getDescription();
        this.tipoCargo = cargo.getTipoCargo() != null ? cargo.getTipoCargo().name() : null;
        this.anticipoDiario = cargo.getAnticipoDiario();
        this.salarioEscala = cargo.getSalarioEscala();
        this.taza = cargo.getTaza();
    }
}
