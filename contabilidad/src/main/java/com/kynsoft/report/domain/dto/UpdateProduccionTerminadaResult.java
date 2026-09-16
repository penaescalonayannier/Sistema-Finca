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
public class UpdateProduccionTerminadaResult {
    private UUID id;
    private Double stockAnterior;
    private Double stockNuevo;
    private Double ajuste;

    public static class UpdateProduccionTerminadaResultBuilder {
        public UpdateProduccionTerminadaResultBuilder ajuste(Number ajuste) {
            this.ajuste = ajuste != null ? ajuste.doubleValue() : null;
            return this;
        }
    }
}
