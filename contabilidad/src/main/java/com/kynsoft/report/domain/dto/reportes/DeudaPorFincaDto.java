package com.kynsoft.report.domain.dto.reportes;

import lombok.*;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DeudaPorFincaDto {
    private UUID fincaId;
    private String fincaCode;
    private String fincaName;
    private Integer trabajadoresConDeuda;
    private Double montoTotal;
}
