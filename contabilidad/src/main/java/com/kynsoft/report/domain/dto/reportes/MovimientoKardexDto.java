package com.kynsoft.report.domain.dto.reportes;

import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MovimientoKardexDto {
    private LocalDateTime fecha;
    private TipoMovimientoStock tipo;
    private Integer cantidad;
    private Integer stockResultante;
    private String referencia;
    private UUID referenciaId;
}
