package com.kynsoft.report.domain.dto.reportes;

import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class VentaDocumentoDto {
    private String numero;
    private LocalDateTime fecha;
    private String tipo;
    private String destino;
    private String fincaNombre;
    private String productoNombre;
    private Integer cantidad;
    private Double valorTotal;
}
