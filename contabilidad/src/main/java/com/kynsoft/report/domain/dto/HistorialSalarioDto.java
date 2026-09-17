package com.kynsoft.report.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Instantánea salarial individual. No interviene en el cálculo de nómina. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class HistorialSalarioDto {
    private UUID id;
    private UUID trabajadorId;
    private UUID fincaId;
    private UUID cargoId;
    private LocalDate fechaVigencia;
    private BigDecimal salarioEscala;
    private BigDecimal anticipoDiario;
    private BigDecimal tasa;
    private String motivo;
    private String estado;
    private UUID autorizadoPorId;
}
