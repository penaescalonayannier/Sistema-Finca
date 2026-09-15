package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class PagoDeudaDto {
    private UUID id;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private String trabajadorRuc;
    private Double monto;
    private FormaPago formaPago;
    private String referenciaBancaria;
    private LocalDateTime fecha;
    private String numeroRecibo;
    private Double saldoAnterior;
    private Double saldoNuevo;
    private String concepto;
    private UUID fincaId;
    private String fincaName;
}
