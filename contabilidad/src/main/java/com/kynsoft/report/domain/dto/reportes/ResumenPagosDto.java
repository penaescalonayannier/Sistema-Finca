package com.kynsoft.report.domain.dto.reportes;

import lombok.*;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ResumenPagosDto {
    private PeriodoDto periodo;
    private Map<String, String> filtros;
    private ResumenPagosTotalesDto resumen;
    private List<DetallePagosPorMetodoDto> detallePorMetodo;
    private List<PagoDocumentoDto> documentos;
}
