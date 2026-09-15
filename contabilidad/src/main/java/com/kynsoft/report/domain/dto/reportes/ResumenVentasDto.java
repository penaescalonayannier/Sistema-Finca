package com.kynsoft.report.domain.dto.reportes;

import lombok.*;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ResumenVentasDto {
    private PeriodoDto periodo;
    private Map<String, String> filtros;
    private ResumenVentasTotalesDto resumen;
    private List<DetalleVentasPorDestinoDto> detallePorDestino;
    private List<VentaDocumentoDto> documentos;
}
