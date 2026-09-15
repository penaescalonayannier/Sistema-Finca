package com.kynsoft.report.domain.dto.reportes;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ReporteDeudasPendientesDto {
    private LocalDate fecha;
    private ResumenDeudasDto resumen;
    private List<DeudaPorFincaDto> porFinca;
    private List<DeudaDetalleDto> deudas;
}
