package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para Asiento Contable.
 * Generado automáticamente desde MovimientoStock.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsientoContableDto {

    private UUID id;
    private String numero;
    private LocalDate fecha;
    private String descripcion;

    // Trazabilidad al movimiento físico origen
    private UUID movimientoStockId;
    private String tablaOrigen;

    // Totales
    private BigDecimal totalDebe;
    private BigDecimal totalHaber;

    // Control
    private Boolean asentado;
    private LocalDateTime fechaAsentado;
    private String usuarioAsento;

    // Regla aplicada
    private UUID reglaId;

    // Líneas del asiento
    private List<LineaAsientoDto> lineas;
}
