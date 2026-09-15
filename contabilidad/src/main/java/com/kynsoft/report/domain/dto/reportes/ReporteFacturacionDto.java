package com.kynsoft.report.domain.dto.reportes;

import lombok.*;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ReporteFacturacionDto {
    private PeriodoDto periodo;
    private Map<String, String> filtros;
    private ResumenFacturacionDto resumen;
    private List<DetalleFacturacionDto> detalle;
    private List<DocumentoFacturacionDto> documentos;
}
