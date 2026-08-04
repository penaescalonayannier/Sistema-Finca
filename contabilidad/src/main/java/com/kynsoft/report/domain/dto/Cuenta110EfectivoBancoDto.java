package com.kynsoft.report.domain.dto;

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
public class Cuenta110EfectivoBancoDto {
    private UUID id;
    private String observaciones;
    private Double importe;
}
