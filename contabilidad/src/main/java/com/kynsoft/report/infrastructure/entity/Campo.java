package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.CampoDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "campo")  // ← Agregar @Table
public class Campo {

    @Id
    @Column(name = "id")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bloque_id")
    private Bloque bloque;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cepa_id")
    private Cepa cepa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "variedad_id")
    private Variedad variedad;
    
    @Column(name = "campo")
    private String campo;
    
    @Column(name = "area")
    private Double area;
    
    @Column(name = "poblacion")
    private Double poblacion;
    
    @Column(name = "destino")
    private String destino;

    @Column(name = "rendimiento")
    private Double rendimiento;

    @Column(name = "valor_adquisicion")
    private Double valorAdquisicion;

    @Column(name = "depreciacion_acumulada")
    private Double depreciacionAcumulada;

    @Column(name = "valor_residual")
    private Double valorResidual;

    @Column(name = "anos_cepa")
    private Integer anosCepa;

    @Column(name = "tasa_depreciacion_anual")
    private Double tasaDepreciacionAnual;

    @Column(name = "vida_util_anios")
    private Integer vidaUtilAnios;

    @Column(name = "fecha_ultima_depreciacion")
    private LocalDate fechaUltimaDepreciacion;

    @Column(name = "fecha_inicio_depreciacion")
    private LocalDate fechaInicioDepreciacion;

    public Campo(CampoDto dto) {
        this.id = dto.getId();
        this.bloque = dto.getBloque() != null ? new Bloque(dto.getBloque()) : null;
        this.variedad = dto.getVariedad() != null ? new Variedad(dto.getVariedad()) : null;
        this.cepa = dto.getCepa() != null ? new Cepa(dto.getCepa()) : null;
        this.campo = dto.getCampo();
        this.area = dto.getArea();
        this.poblacion = dto.getPoblacion();
        this.destino = dto.getDestino();
        this.rendimiento = dto.getRendimiento();
        this.valorAdquisicion = dto.getValorAdquisicion();
        this.depreciacionAcumulada = dto.getDepreciacionAcumulada();
        this.valorResidual = dto.getValorResidual();
        this.anosCepa = dto.getAnosCepa();
        this.tasaDepreciacionAnual = dto.getTasaDepreciacionAnual();
        this.vidaUtilAnios = dto.getVidaUtilAnios();
        this.fechaUltimaDepreciacion = dto.getFechaUltimaDepreciacion();
        this.fechaInicioDepreciacion = dto.getFechaInicioDepreciacion();
    }

    public CampoDto toAggregate() {
        // Calculate valor actual (valor adquisición - depreciación acumulada)
        Double valorActual = null;
        if (valorAdquisicion != null && depreciacionAcumulada != null) {
            valorActual = valorAdquisicion - depreciacionAcumulada;
        }

        return CampoDto
                .builder()
                .id(id)
                .bloque(bloque != null ? bloque.toAggregate() : null)
                .campo(campo)
                .area(area)
                .variedad(variedad != null ? variedad.toAggregate() : null)
                .cepa(cepa != null ? cepa.toAggregate() : null)
                .poblacion(poblacion)
                .destino(destino)
                .rendimiento(rendimiento)
                .valorAdquisicion(valorAdquisicion)
                .depreciacionAcumulada(depreciacionAcumulada)
                .valorResidual(valorResidual)
                .valorActual(valorActual)
                .anosCepa(anosCepa)
                .tasaDepreciacionAnual(tasaDepreciacionAnual)
                .vidaUtilAnios(vidaUtilAnios)
                .fechaUltimaDepreciacion(fechaUltimaDepreciacion)
                .fechaInicioDepreciacion(fechaInicioDepreciacion)
                .build();
    }
}