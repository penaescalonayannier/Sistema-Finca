package com.kynsoft.report.domain.dto;

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
public class ProduccionTerminadaDto {
    private UUID id;
    private String fecha;
    private String recibidoPor;
    private String entregadoPor;
    private String consecutivo;
}
