package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import lombok.Builder;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductoProduccionTerminadaDto {
    private UUID id;
    private ProduccionTerminadaDto produccionTerminada;
    private ProductoDto producto;
    private Double cantidad;
}