package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.LaborDto;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Labor {
    @Id
    @Column(name = "id")
    private UUID id;
    private String code;
    private String name;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "tipo_cultivo_id")
    private UUID tipoCultivoId;

    @Column(name = "codigo_centro_costo", length = 20)
    private String codigoCentroCosto;

    @Column(name = "unidad_medida", length = 30)
    private String unidadMedida;

    @Column(name = "norma_rendimiento", precision = 19, scale = 4)
    private BigDecimal normaRendimiento;

    @Column(name = "indice_consumo", precision = 19, scale = 4)
    private BigDecimal indiceConsumo;

    @Column(name = "nivel_actividad", length = 100)
    private String nivelActividad;

    /** Trabajadores requeridos por unidad de área/rendimiento. */
    @Column(name = "dotacion_estandar", precision = 19, scale = 4)
    private BigDecimal dotacionEstandar;

    @Column(name = "dias_estimados", precision = 19, scale = 4)
    private BigDecimal diasEstimados;

    @Column(name = "horas_estimadas", precision = 19, scale = 4)
    private BigDecimal horasEstimadas;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "labor_cargo_requerido", joinColumns = @JoinColumn(name = "labor_id"))
    @Column(name = "cargo_id", nullable = false)
    private Set<UUID> cargosRequeridos = new LinkedHashSet<>();

    public Labor(LaborDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
        this.tipoCultivoId = dto.getTipoCultivoId();
        this.codigoCentroCosto = dto.getCodigoCentroCosto();
        this.unidadMedida = dto.getUnidadMedida();
        this.normaRendimiento = dto.getNormaRendimiento();
        this.indiceConsumo = dto.getIndiceConsumo();
        this.nivelActividad = dto.getNivelActividad();
        this.dotacionEstandar = dto.getDotacionEstandar();
        this.diasEstimados = dto.getDiasEstimados();
        this.horasEstimadas = dto.getHorasEstimadas();
        this.cargosRequeridos = dto.getCargosRequeridos() == null ? new LinkedHashSet<>() : new LinkedHashSet<>(dto.getCargosRequeridos());
    }

    public LaborDto toAggregate() {
        return LaborDto
                .builder()
                .id(id)
                .code(code)
                .name(name)
                .activo(activo)
                .tipoCultivoId(tipoCultivoId)
                .codigoCentroCosto(codigoCentroCosto)
                .unidadMedida(unidadMedida)
                .normaRendimiento(normaRendimiento)
                .indiceConsumo(indiceConsumo)
                .nivelActividad(nivelActividad)
                .dotacionEstandar(dotacionEstandar)
                .diasEstimados(diasEstimados)
                .horasEstimadas(horasEstimadas)
                .cargosRequeridos(cargosRequeridos == null ? Set.of() : new LinkedHashSet<>(cargosRequeridos))
                .build();
    }
}
