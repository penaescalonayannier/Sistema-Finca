package com.kynsoft.report.domain.dto.reportes;

import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class PagoDocumentoDto {
    private String numeroRecibo;
    private LocalDateTime fecha;
    private String trabajadorNombre;
    private String trabajadorRuc;
    private String fincaNombre;
    private Double monto;
    private String formaPago;
    private String referenciaBancaria;
}
