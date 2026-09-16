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
    private Double stockAnterior;
    private Double stockNuevo;
    private Double cantidadRevertida;

    public static class DeleteProduccionTerminadaResultBuilder {
        public DeleteProduccionTerminadaResultBuilder cantidadRevertida(Number cantidadRevertida) {
            this.cantidadRevertida = cantidadRevertida != null ? cantidadRevertida.doubleValue() : null;
            return this;
        }
    }
}
