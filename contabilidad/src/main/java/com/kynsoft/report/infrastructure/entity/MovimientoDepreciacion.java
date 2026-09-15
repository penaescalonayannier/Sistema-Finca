package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.MovimientoDepreciacionDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Movimiento de Depreciación - Registro del cálculo mensual de depreciación.
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP): Cálculo de depreciación
 * - Resolución No. 51/2021 MFP: Tasas máximas de depreciación
 *
 * Método de depreciación según NCC No. 7:
 * Se utiliza el método de línea recta:
 *
 * Depreciación Anual = (Valor Adquisición - Valor Residual Estimado) × Tasa%
 * Depreciación Mensual = Depreciación Anual / 12
 *
 * Este registro permite:
 * - Auditar el cálculo de depreciación mes a mes
 * - Generar reportes de depreciación acumulada
 * - Cumplir con los requisitos de control interno (Resolución 60/2011 CGR)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "movimiento_depreciacion")
public class MovimientoDepreciacion {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activo_fijo_id", nullable = false)
    private ActivoFijoTangible activoFijo;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    /**
     * Monto de depreciación calculado para este período.
     */
    @Column(name = "monto_depreciacion", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoDepreciacion;

    /**
     * Depreciación acumulada antes de este movimiento.
     */
    @Column(name = "depreciacion_acumulada_anterior", precision = 15, scale = 2)
    private BigDecimal depreciacionAcumuladaAnterior;

    /**
     * Depreciación acumulada después de este movimiento.
     */
    @Column(name = "depreciacion_acumulada_nueva", precision = 15, scale = 2)
    private BigDecimal depreciacionAcumuladaNueva;

    /**
     * Valor residual resultante después de aplicar la depreciación.
     */
    @Column(name = "valor_residual_resultante", precision = 15, scale = 2)
    private BigDecimal valorResidualResultante;

    /**
     * Tasa de depreciación aplicada (para auditoría).
     */
    @Column(name = "tasa_aplicada", precision = 5, scale = 2)
    private BigDecimal tasaAplicada;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public MovimientoDepreciacion(MovimientoDepreciacionDto dto) {
        this.id = dto.getId();
        this.fecha = dto.getFecha();
        this.mes = dto.getMes();
        this.anio = dto.getAnio();
        this.montoDepreciacion = dto.getMontoDepreciacion();
        this.depreciacionAcumuladaAnterior = dto.getDepreciacionAcumuladaAnterior();
        this.depreciacionAcumuladaNueva = dto.getDepreciacionAcumuladaNueva();
        this.valorResidualResultante = dto.getValorResidualResultante();
        this.tasaAplicada = dto.getTasaAplicada();
        this.observacion = dto.getObservacion();
    }

    public MovimientoDepreciacionDto toAggregate() {
        return MovimientoDepreciacionDto.builder()
                .id(id)
                .activoFijoId(activoFijo != null ? activoFijo.getId() : null)
                .numeroInventario(activoFijo != null ? activoFijo.getNumeroInventario() : null)
                .descripcionActivo(activoFijo != null ? activoFijo.getDescripcion() : null)
                .fecha(fecha)
                .mes(mes)
                .anio(anio)
                .montoDepreciacion(montoDepreciacion)
                .depreciacionAcumuladaAnterior(depreciacionAcumuladaAnterior)
                .depreciacionAcumuladaNueva(depreciacionAcumuladaNueva)
                .valorResidualResultante(valorResidualResultante)
                .tasaAplicada(tasaAplicada)
                .observacion(observacion)
                .createdAt(createdAt)
                .build();
    }
}
