package com.kynsoft.report.domain.dto.reportes;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DocumentoFacturacionDto {
    private UUID id;
    private String numero;
    private LocalDate fecha;
    private String producto;
    private Double cantidad;
    private String destino;
    private Double valor;
}
