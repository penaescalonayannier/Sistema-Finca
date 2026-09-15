package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.ActivoFijoTangibleDto;
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
 * Activo Fijo Tangible según NCC No. 7 (Resolución 1038/2017 MFP).
 *
 * Normativa aplicable:
 * - Resolución No. 1038/2017 del MFP: NCC No. 7 "Activos Fijos Tangibles"
 * - Resolución No. 51/2021 del MFP: Tasas máximas de depreciación
 * - Resolución No. 60/2011 de la CGR: Control interno de recursos materiales
 *
 * Según NCC No. 7, un activo fijo tangible debe:
 * - Ser propiedad de la entidad o estar bajo su control
 * - Tener una vida útil mayor a un año
 * - Ser utilizado en la producción o suministro de bienes/servicios
 * - No estar destinado a la venta en el curso normal de las operaciones
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "activo_fijo_tangible")
public class ActivoFijoTangible {

    @Id
    @Column(name = "id")
    private UUID id;

    /**
     * Número de inventario único del activo.
     * Formato típico: GG-NNNN (ej: 01-0008, 04-1503)
     */
    @Column(name = "numero_inventario", nullable = false, length = 50)
    private String numeroInventario;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "grupo_id")
    private GrupoActivoFijo grupo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "finca_id")
    private Finca finca;

    /**
     * Valor de adquisición o costo histórico según NCC No. 7.
     */
    @Column(name = "valor_adquisicion", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorAdquisicion;

    /**
     * Depreciación acumulada según método de línea recta (NCC No. 7).
     */
    @Column(name = "depreciacion_acumulada", precision = 15, scale = 2)
    private BigDecimal depreciacionAcumulada = BigDecimal.ZERO;

    /**
     * Valor residual = Valor Adquisición - Depreciación Acumulada.
     * Según NCC No. 7, es el importe neto que la entidad espera obtener
     * al final de la vida útil del activo.
     */
    @Column(name = "valor_residual", precision = 15, scale = 2)
    private BigDecimal valorResidual;

    /**
     * Estado técnico del activo expresado en porcentaje (0-100%).
     * Usado para valuación y determinación de deterioro según NCC No. 7.
     */
    @Column(name = "estado_tecnico_porcentaje")
    private Integer estadoTecnicoPorcentaje;

    /**
     * Valor de tasación o valor de mercado para efectos de venta.
     */
    @Column(name = "valor_tasacion", precision = 15, scale = 2)
    private BigDecimal valorTasacion;

    @Column(name = "fecha_adquisicion")
    private LocalDate fechaAdquisicion;

    /**
     * Fecha de baja del activo. Si es null, el activo está activo.
     */
    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    /**
     * Destino o ubicación del activo (ej: "ECTE", "Almacén", etc.)
     */
    @Column(name = "destino", length = 50)
    private String destino;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calcularValorResidual();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calcularValorResidual();
    }

    /**
     * Calcula el valor residual según fórmula NCC No. 7.
     */
    public void calcularValorResidual() {
        if (valorAdquisicion != null) {
            BigDecimal depreciacion = depreciacionAcumulada != null ? depreciacionAcumulada : BigDecimal.ZERO;
            this.valorResidual = valorAdquisicion.subtract(depreciacion);
        }
    }

    public ActivoFijoTangible(ActivoFijoTangibleDto dto) {
        this.id = dto.getId();
        this.numeroInventario = dto.getNumeroInventario();
        this.descripcion = dto.getDescripcion();
        this.valorAdquisicion = dto.getValorAdquisicion();
        this.depreciacionAcumulada = dto.getDepreciacionAcumulada() != null ? dto.getDepreciacionAcumulada() : BigDecimal.ZERO;
        this.valorResidual = dto.getValorResidual();
        this.estadoTecnicoPorcentaje = dto.getEstadoTecnicoPorcentaje();
        this.valorTasacion = dto.getValorTasacion();
        this.fechaAdquisicion = dto.getFechaAdquisicion();
        this.fechaBaja = dto.getFechaBaja();
        this.destino = dto.getDestino();
        this.observaciones = dto.getObservaciones();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
    }

    public ActivoFijoTangibleDto toAggregate() {
        return ActivoFijoTangibleDto.builder()
                .id(id)
                .numeroInventario(numeroInventario)
                .descripcion(descripcion)
                .grupoId(grupo != null ? grupo.getId() : null)
                .grupoCodigo(grupo != null ? grupo.getCodigo() : null)
                .grupoNombre(grupo != null ? grupo.getNombre() : null)
                .fincaId(finca != null ? finca.getId() : null)
                .fincaNombre(finca != null ? finca.getName() : null)
                .valorAdquisicion(valorAdquisicion)
                .depreciacionAcumulada(depreciacionAcumulada)
                .valorResidual(valorResidual)
                .estadoTecnicoPorcentaje(estadoTecnicoPorcentaje)
                .valorTasacion(valorTasacion)
                .fechaAdquisicion(fechaAdquisicion)
                .fechaBaja(fechaBaja)
                .destino(destino)
                .observaciones(observaciones)
                .activo(activo)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
