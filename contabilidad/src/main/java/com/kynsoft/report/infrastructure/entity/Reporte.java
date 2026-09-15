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

    /**
     * Tipo de reporte (nomenclador). Determina centro de costo y subclasificación.
     */
    @Column(name = "tipo_reporte_id")
    private UUID tipoReporteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_reporte_id", insertable = false, updatable = false)
    private TipoReporte tipoReporte;

    /**
     * Tipo de cultivo (nomenclador). Determina si requiere bloque/campo.
     */
    @Column(name = "tipo_cultivo_id")
    private UUID tipoCultivoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_cultivo_id", insertable = false, updatable = false)
    private TipoCultivo tipoCultivo;

    /**
     * Tipo de animal (nomenclador). Para reportes de vaquería.
     */
    @Column(name = "tipo_animal_id")
    private UUID tipoAnimalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_animal_id", insertable = false, updatable = false)
    private TipoAnimal tipoAnimal;

    /**
     * Bloque - ahora nullable (solo requerido si tipoCultivo.requiereCampo = true)
     */
    @Column(name = "bloque", nullable = true, length = 50)
    private String bloque;

    /**
     * Campo - ahora nullable (solo requerido si tipoCultivo.requiereCampo = true)
     */
    @Column(name = "campo", nullable = true, length = 50)
    private String campo;

    @Column(name = "area", nullable = true, length = 50)
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

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public Reporte(ReporteDto dto) {
        this.id = dto.getId();
        this.tipoReporteId = dto.getTipoReporteId();
        this.tipoCultivoId = dto.getTipoCultivoId();
        this.tipoAnimalId = dto.getTipoAnimalId();
        this.bloque = dto.getBloque();
        this.campo = dto.getCampo();
        this.area = dto.getArea();
        this.norma = dto.getNorma();
        this.codigo = dto.getCodigo();
        this.year = dto.getYear();
        this.mes = dto.getMes();
        this.fecha = dto.getFecha();
        this.trabajadorResponsableId = dto.getTrabajadorResponsableId();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
        // NOTA: No se asignan los días aquí para evitar recursión
    }

    public ReporteDto toAggregate() {
        return ReporteDto.builder()
                .id(id)
                // Tipo de reporte
                .tipoReporteId(tipoReporteId)
                .tipoReporteCodigo(tipoReporte != null ? tipoReporte.getCodigo() : null)
                .tipoReporteNombre(tipoReporte != null ? tipoReporte.getNombre() : null)
                .tipoSubclasificacion(tipoReporte != null ? tipoReporte.getTipoSubclasificacion() : null)
                .tipoCultivoCategoriaFiltro(tipoReporte != null ? tipoReporte.getTipoCultivoCategoriaFiltro() : null)
                // Tipo de cultivo
                .tipoCultivoId(tipoCultivoId)
                .tipoCultivoNombre(tipoCultivo != null ? tipoCultivo.getNombre() : null)
                .tipoCultivoRequiereCampo(tipoCultivo != null ? tipoCultivo.getRequiereCampo() : null)
                // Tipo de animal
                .tipoAnimalId(tipoAnimalId)
                .tipoAnimalNombre(tipoAnimal != null ? tipoAnimal.getNombre() : null)
                // Bloque y campo
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
                .activo(activo)
                .build();
    }
}