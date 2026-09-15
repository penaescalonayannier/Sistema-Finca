package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.ActivoAnimalDto;
import com.kynsoft.report.domain.dto.enums.CategoriaAnimal;
import com.kynsoft.report.domain.dto.enums.TipoGanado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Activo Animal - Grupo 08 según NCC No. 7.
 * Tabla independiente para facilitar el registro de inventario ganadero.
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP): Grupo 08 - Animales
 * - Certificación de Tenencia de Ganado Mayor (Delegación Agricultura Municipal)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "activo_animal")
public class ActivoAnimal {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "numero_inventario", length = 50)
    private String numeroInventario;

    @Column(name = "codigo_arete", length = 50)
    private String codigoArete;

    @Column(name = "hierro", length = 50)
    private String hierro;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 50)
    private CategoriaAnimal categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ganado", nullable = false, length = 50)
    private TipoGanado tipoGanado;

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

    @Column(name = "anios_vida")
    private Integer aniosVida;

    @Column(name = "peso_promedio", precision = 10, scale = 2)
    private BigDecimal pesoPromedio;

    @Column(name = "destino", length = 255)
    private String destino;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ActivoAnimal(ActivoAnimalDto dto) {
        this.id = dto.getId();
        this.numeroInventario = dto.getNumeroInventario();
        this.codigoArete = dto.getCodigoArete();
        this.hierro = dto.getHierro();
        this.categoria = dto.getCategoria();
        this.tipoGanado = dto.getTipoGanado();
        this.fincaId = dto.getFincaId();
        this.valorAdquisicion = dto.getValorAdquisicion();
        this.depreciacionAcumulada = dto.getDepreciacionAcumulada();
        this.valorResidual = dto.getValorResidual();
        this.valorTasacion = dto.getValorTasacion();
        this.aniosVida = dto.getAniosVida();
        this.pesoPromedio = dto.getPesoPromedio();
        this.destino = dto.getDestino();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
        this.createdAt = dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ActivoAnimalDto toAggregate() {
        return ActivoAnimalDto.builder()
                .id(id)
                .numeroInventario(numeroInventario)
                .codigoArete(codigoArete)
                .hierro(hierro)
                .categoria(categoria)
                .tipoGanado(tipoGanado)
                .fincaId(fincaId)
                .fincaNombre(finca != null ? finca.getName() : null)
                .valorAdquisicion(valorAdquisicion)
                .depreciacionAcumulada(depreciacionAcumulada)
                .valorResidual(valorResidual)
                .valorTasacion(valorTasacion)
                .aniosVida(aniosVida)
                .pesoPromedio(pesoPromedio)
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
