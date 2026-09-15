package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.AlmacenDto;
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
@Table(name = "almacenes")
public class Almacen {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "inventario", unique = true, nullable = false, length = 50)
    private String inventario;

    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal = false;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finca_id")
    private Finca finca;

    @OneToMany(mappedBy = "almacen", fetch = FetchType.LAZY)
    private List<AlmacenFincaProducto> almacenProductos = new ArrayList<>();

    // Legacy: para compatibilidad con código existente que use productos directamente
    @Transient
    public List<FincaProducto> getProductos() {
        if (almacenProductos == null) return new ArrayList<>();
        return almacenProductos.stream()
                .filter(afp -> afp.getActivo() != null && afp.getActivo())
                .map(AlmacenFincaProducto::getFincaProducto)
                .collect(java.util.stream.Collectors.toList());
    }

    public Almacen(AlmacenDto dto) {
        this.id = dto.getId();
        this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion();
        this.inventario = dto.getInventario();
        this.esPrincipal = dto.getEsPrincipal() != null ? dto.getEsPrincipal() : false;
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
    }

    public AlmacenDto toAggregate() {
        return AlmacenDto.builder()
                .id(id)
                .nombre(nombre)
                .descripcion(descripcion)
                .inventario(inventario)
                .esPrincipal(esPrincipal)
                .activo(activo)
                .fincaId(finca != null ? finca.getId() : null)
                .fincaCode(finca != null ? finca.getCode() : null)
                .fincaName(finca != null ? finca.getName() : null)
                .build();
    }
}
