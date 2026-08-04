package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.CampoDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    
    // ✅ Cambiar de "private Bloque bloque" a relación JPA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloque_id")
    private Bloque bloque;
    
    // ✅ Cambiar de "private Cepa cepa" a relación JPA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cepa_id")
    private Cepa cepa;
    
    // ✅ Cambiar de "private Variedad variedad" a relación JPA
    @ManyToOne(fetch = FetchType.LAZY)
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
    }

    public CampoDto toAggregate() {
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
                .build();
    }
}