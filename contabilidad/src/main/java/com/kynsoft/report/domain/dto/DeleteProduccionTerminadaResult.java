package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DeleteProduccionTerminadaResult {
    private UUID id;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private Integer cantidadRevertida;
}
