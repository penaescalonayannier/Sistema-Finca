package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Cantidad física de billetes CUP para una denominación. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DenominacionCajaDto {
    private Integer denominacion;
    private Integer cantidad;
}
