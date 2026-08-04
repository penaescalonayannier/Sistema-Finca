package com.kynsoft.report.domain.dto;

import com.kynsoft.report.domain.dto.enumerativos.TipoCargo;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CargoDto {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal salarioEscala;
    private BigDecimal anticipoDiario; // Calculated field
    private BigDecimal taza; // Calculated field
    private TipoCargo tipoCargo; // Calculated field
}
