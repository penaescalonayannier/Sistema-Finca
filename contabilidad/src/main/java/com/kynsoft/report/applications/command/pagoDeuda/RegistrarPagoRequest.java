package com.kynsoft.report.applications.command.pagoDeuda;

import com.kynsoft.report.domain.dto.FormaPago;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RegistrarPagoRequest {
    private UUID trabajadorId;
    private Double monto;
    private FormaPago formaPago;
    private String referenciaBancaria;
}
