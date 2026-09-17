package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntregaBancoResponse {
    private UUID id;
    private UUID fincaId;
    private String numeroDocumento;
    private LocalDateTime fecha;
    private Double importe;
    private String referenciaBancaria;
    private String entregadoPor;
    private String recibidoPor;
    private String observaciones;
    private List<DenominacionCajaDto> denominaciones;
}
