package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.CategoriaTipoCultivo;
import com.kynsoft.report.domain.dto.TipoCultivoDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Nomenclador de tipos de cultivo.
 * Define si el cultivo requiere selección de Bloque/Campo (como la Caña)
 * o si es un cultivo varios (Yuca, Maíz, Boniato, etc.)
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tipo_cultivo")
public class TipoCultivo {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    /**
     * Categoría del cultivo: CANNA, VIANDA, OTRO
     * Permite filtrar cultivos según el tipo de reporte
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 20)
    private CategoriaTipoCultivo categoria = CategoriaTipoCultivo.OTRO;

    /**
     * Indica si este tipo de cultivo requiere selección de Bloque y Campo.
     * true = Caña (requiere bloque/campo)
     * false = Cultivos varios (Yuca, Maíz, etc. - no requiere bloque/campo)
     */
    @Column(name = "requiere_campo", nullable = false)
    private Boolean requiereCampo = false;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    /**
     * Orden de visualización en listas
     */
    @Column(name = "orden")
    private Integer orden;

    public TipoCultivo(TipoCultivoDto dto) {
        this.id = dto.getId();
        this.codigo = dto.getCodigo();
        this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion();
        this.categoria = dto.getCategoria() != null ? dto.getCategoria() : CategoriaTipoCultivo.OTRO;
        this.requiereCampo = dto.getRequiereCampo() != null ? dto.getRequiereCampo() : false;
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
        this.orden = dto.getOrden();
    }

    public TipoCultivoDto toAggregate() {
        return TipoCultivoDto.builder()
                .id(id)
                .codigo(codigo)
                .nombre(nombre)
                .descripcion(descripcion)
                .categoria(categoria)
                .requiereCampo(requiereCampo)
                .activo(activo)
                .orden(orden)
                .build();
    }
}
