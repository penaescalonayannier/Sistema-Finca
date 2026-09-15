package com.kynsoft.report.domain.dto.reportes;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DeudaDetalleDto {
    private UUID trabajadorId;
    private String trabajadorNombre;
    private String trabajadorRuc;
    private UUID fincaId;
    private String fincaName;
    private Double monto;
    private LocalDate ultimoPagoFecha;
    private Double ultimoPagoMonto;
}
