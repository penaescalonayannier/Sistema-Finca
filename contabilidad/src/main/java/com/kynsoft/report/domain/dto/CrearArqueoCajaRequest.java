package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearArqueoCajaRequest {
    private UUID fincaId;
    private String contadorResponsable;
    private String custodio;
    private String recibidoPor;
    private String observaciones;
    /** Denominaciones a revisar; si no se informa se toma toda la caja física registrada. */
    private List<Integer> muestraDenominaciones;
}
