package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.ReporteDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "reporte")
public class Reporte {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "bloque", nullable = false, length = 50)
    private String bloque;

    @Column(name = "campo", nullable = false, length = 50)
    private String campo;

    @Column(name = "area", nullable = false, length = 50)
    private String area;

    @Column(name = "norma", nullable = false, length = 50)
    private String norma;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "year", nullable = false, length = 50)
    private String year;

    @Column(name = "mes", nullable = false, length = 50)
    private String mes;

    @Column(name = "fecha", nullable = true)
    private String fecha;

    @Column(name = "trabajador_responsable_id")
    private UUID trabajadorResponsableId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trabajador_responsable_id", insertable = false, updatable = false)
    private Trabajador trabajadorResponsable;

    @OneToMany(mappedBy = "reporte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaTrabajo> dias = new ArrayList<>();

    public Reporte(ReporteDto dto) {
        this.id = dto.getId();
        this.bloque = dto.getBloque();
        this.campo = dto.getCampo();
        this.area = dto.getArea();
        this.norma = dto.getNorma();
        this.codigo = dto.getCodigo();
        this.year = dto.getYear();
        this.mes = dto.getMes();
        this.fecha = dto.getFecha();
        this.trabajadorResponsableId = dto.getTrabajadorResponsableId();
        // NOTA: No se asignan los días aquí para evitar recursión
    }

    public ReporteDto toAggregate() {
        return ReporteDto.builder()
                .id(id)
                .bloque(bloque)
                .campo(campo)
                .area(area)
                .norma(norma)
                .codigo(codigo)
                .year(year)
                .mes(mes)
                .fecha(fecha)
                .trabajadorResponsableId(trabajadorResponsableId)
                .trabajadorResponsableNombre(trabajadorResponsable != null ? trabajadorResponsable.getNombre() : null)
                .build();
    }
}