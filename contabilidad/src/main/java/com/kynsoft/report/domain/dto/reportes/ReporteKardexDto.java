package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ReporteKardexDto {
    private FincaInfoDto finca;
    private PeriodoDto periodo;
    private List<ProductoKardexDto> productos;
    private TotalesKardexDto totales;
}
