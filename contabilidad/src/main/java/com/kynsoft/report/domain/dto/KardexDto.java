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
    private Double stockInicial;
    private List<MovimientoKardexDto> movimientos;
    private Double stockFinal;
    private Double totalEntradas;
    private Double totalSalidas;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoInfoDto {
        private UUID fincaProductoId;
        private UUID productoId;
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
        private Double entrada;
        private Double salida;
        private Double saldo;
        private String observaciones;
        /** Documento que originó el movimiento; se muestra en la tarjeta de estiba. */
        private UUID referenciaId;
        private String referenciaTabla;
        private String descripcion;
    }
}
