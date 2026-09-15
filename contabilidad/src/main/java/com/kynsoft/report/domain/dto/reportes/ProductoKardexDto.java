package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductoKardexDto {
    private UUID productoId;
    private String productoCode;
    private String productoName;
    private String unidadMedida;
    private Integer stockInicial;
    private EntradasSalidasDto entradas;
    private EntradasSalidasDto salidas;
    private Integer stockFinal;
    private List<MovimientoKardexDto> movimientos;
}
