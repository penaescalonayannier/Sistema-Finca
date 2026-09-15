package com.kynsoft.report.domain.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KardexDto {

    private ProductoInfoDto producto;
    private AlmacenInfoDto almacen;
    private Integer stockInicial;
    private List<MovimientoKardexDto> movimientos;
    private Integer stockFinal;
    private Long totalEntradas;
    private Long totalSalidas;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoInfoDto {
        private UUID fincaProductoId;
        private String productoCode;
        private String productoName;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlmacenInfoDto {
        private UUID almacenId;
        private String almacenNombre;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovimientoKardexDto {
        private LocalDateTime fecha;
        private TipoMovimientoStock tipoMovimiento;
        private Integer entrada;
        private Integer salida;
        private Integer saldo;
        private String observaciones;
    }
}
