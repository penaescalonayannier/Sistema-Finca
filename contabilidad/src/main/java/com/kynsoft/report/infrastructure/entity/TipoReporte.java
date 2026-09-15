package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.CategoriaTipoCultivo;
import com.kynsoft.report.domain.dto.TipoReporteDto;
import com.kynsoft.report.domain.dto.TipoSubclasificacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Nomenclador de tipos de reporte.
 * Define el centro de costo contable y qué subclasificación usar.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tipo_reporte")
public class TipoReporte {

    @Id
    @Column(name = "id")
    private UUID id;

    /**
     * Código único: REPORTE_CANNA, REPORTE_PLAN_VIANDA, etc.
     */
    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    /**
     * Código del centro de costo contable asociado
     */
    @Column(name = "codigo_centro_costo", length = 50)
    private String codigoCentroCosto;

    /**
     * Tipo de subclasificación: CULTIVO, ANIMAL, NINGUNO
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_subclasificacion", nullable = false, length = 20)
    private TipoSubclasificacion tipoSubclasificacion = TipoSubclasificacion.NINGUNO;

    /**
     * Categoría de TipoCultivo a filtrar (solo si tipoSubclasificacion = CULTIVO)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cultivo_categoria_filtro", length = 20)
    private CategoriaTipoCultivo tipoCultivoCategoriaFiltro;

    /**
     * ID del TipoCultivo a auto-seleccionar (solo si aplica)
     */
    @Column(name = "tipo_cultivo_auto_id")
    private UUID tipoCultivoAutoId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_cultivo_auto_id", insertable = false, updatable = false)
    private TipoCultivo tipoCultivoAuto;

    /**
     * Indica si requiere Bloque y Campo
     */
    @Column(name = "requiere_campo", nullable = false)
    private Boolean requiereCampo = false;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "orden")
    private Integer orden;

    public TipoReporte(TipoReporteDto dto) {
        this.id = dto.getId();
        this.codigo = dto.getCodigo();
        this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion();
        this.codigoCentroCosto = dto.getCodigoCentroCosto();
        this.tipoSubclasificacion = dto.getTipoSubclasificacion() != null
            ? dto.getTipoSubclasificacion() : TipoSubclasificacion.NINGUNO;
        this.tipoCultivoCategoriaFiltro = dto.getTipoCultivoCategoriaFiltro();
        this.tipoCultivoAutoId = dto.getTipoCultivoAutoId();
        this.requiereCampo = dto.getRequiereCampo() != null ? dto.getRequiereCampo() : false;
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
        this.orden = dto.getOrden();
    }

    public TipoReporteDto toAggregate() {
        return TipoReporteDto.builder()
                .id(id)
                .codigo(codigo)
                .nombre(nombre)
                .descripcion(descripcion)
                .codigoCentroCosto(codigoCentroCosto)
                .tipoSubclasificacion(tipoSubclasificacion)
                .tipoCultivoCategoriaFiltro(tipoCultivoCategoriaFiltro)
                .tipoCultivoAutoId(tipoCultivoAutoId)
                .tipoCultivoAutoNombre(tipoCultivoAuto != null ? tipoCultivoAuto.getNombre() : null)
                .requiereCampo(requiereCampo)
                .activo(activo)
                .orden(orden)
                .build();
    }
}
