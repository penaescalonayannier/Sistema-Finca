package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class DeudaTrabajadorDto {
    private UUID id;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private String trabajadorRuc;
    private Double importe;
}
