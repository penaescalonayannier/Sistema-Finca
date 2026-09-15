package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.PlantacionPermanenteDto;
import com.kynsoft.report.domain.dto.enums.TipoCepa;
import com.kynsoft.report.domain.dto.enums.TipoPlantacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Plantación Permanente - Grupos 12 y 13 según NCC No. 7.
 * Tabla independiente para facilitar el registro de cultivos.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "plantacion_permanente")
public class PlantacionPermanente {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "numero_inventario", length = 50)
    private String numeroInventario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_plantacion", length = 50, nullable = false)
    private TipoPlantacion tipoPlantacion;

    @Column(name = "bloque")
    private Integer bloque;

    @Column(name = "campo")
    private Integer campo;

    @Column(name = "area_hectareas", precision = 10, scale = 4)
    private BigDecimal areaHectareas;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cepa", length = 50)
    private TipoCepa tipoCepa;

    @Column(name = "codigo_variedad", length = 50)
    private String codigoVariedad;

    @Column(name = "anios_cepa")
    private Integer aniosCepa;

    @Column(name = "finca_id")
    private UUID fincaId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "finca_id", insertable = false, updatable = false)
    private Finca finca;

    @Column(name = "valor_adquisicion", precision = 15, scale = 2, nullable = false)
    private BigDecimal valorAdquisicion;

    @Column(name = "depreciacion_acumulada", precision = 15, scale = 2)
    private BigDecimal depreciacionAcumulada;

    @Column(name = "valor_residual", precision = 15, scale = 2)
    private BigDecimal valorResidual;

    @Column(name = "valor_tasacion", precision = 15, scale = 2)
    private BigDecimal valorTasacion;

    @Column(name = "destino", length = 255)
    private String destino;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PlantacionPermanente(PlantacionPermanenteDto dto) {
        this.id = dto.getId();
        this.numeroInventario = dto.getNumeroInventario();
        this.tipoPlantacion = dto.getTipoPlantacion();
        this.bloque = dto.getBloque();
        this.campo = dto.getCampo();
        this.areaHectareas = dto.getAreaHectareas();
        this.tipoCepa = dto.getTipoCepa();
        this.codigoVariedad = dto.getCodigoVariedad();
        this.aniosCepa = dto.getAniosCepa();
        this.fincaId = dto.getFincaId();
        this.valorAdquisicion = dto.getValorAdquisicion();
        this.depreciacionAcumulada = dto.getDepreciacionAcumulada();
        this.valorResidual = dto.getValorResidual();
        this.valorTasacion = dto.getValorTasacion();
        this.destino = dto.getDestino();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
        this.createdAt = dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public PlantacionPermanenteDto toAggregate() {
        return PlantacionPermanenteDto.builder()
                .id(id)
                .numeroInventario(numeroInventario)
                .tipoPlantacion(tipoPlantacion)
                .bloque(bloque)
                .campo(campo)
                .areaHectareas(areaHectareas)
                .tipoCepa(tipoCepa)
                .codigoVariedad(codigoVariedad)
                .aniosCepa(aniosCepa)
                .fincaId(fincaId)
                .fincaNombre(finca != null ? finca.getName() : null)
                .valorAdquisicion(valorAdquisicion)
                .depreciacionAcumulada(depreciacionAcumulada)
                .valorResidual(valorResidual)
                .valorTasacion(valorTasacion)
                .destino(destino)
                .activo(activo)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (activo == null) activo = true;
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
