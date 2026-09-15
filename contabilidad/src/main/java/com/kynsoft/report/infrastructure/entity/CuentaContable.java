package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.CuentaContableDto;
import com.kynsoft.report.domain.dto.NaturalezaCuenta;
import com.kynsoft.report.domain.dto.TipoCuenta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.Hibernate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad Cuenta Contable según Nomenclador Cubano (Res. 494/2016 MFP)
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "cuenta_contable")
public class CuentaContable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "codigo", unique = true, nullable = false, length = 20)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoCuenta tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "naturaleza", nullable = false, length = 10)
    private NaturalezaCuenta naturaleza;

    @Column(name = "nivel", nullable = false)
    private Integer nivel;

    @Column(name = "cuenta_padre_id")
    private UUID cuentaPadreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_padre_id", insertable = false, updatable = false)
    private CuentaContable cuentaPadre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "permite_movimiento")
    private Boolean permiteMovimiento;

    @Column(name = "es_centro_costo")
    private Boolean esCentroCosto;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
        if (permiteMovimiento == null) {
            permiteMovimiento = true;
        }
        if (esCentroCosto == null) {
            esCentroCosto = false;
        }
        if (nivel == null) {
            nivel = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public CuentaContable(CuentaContableDto dto) {
        this.id = dto.getId() != null ? dto.getId() : UUID.randomUUID();
        this.codigo = dto.getCodigo();
        this.nombre = dto.getNombre();
        this.tipo = dto.getTipo();
        this.naturaleza = dto.getNaturaleza();
        this.nivel = dto.getNivel() != null ? dto.getNivel() : 1;
        this.cuentaPadreId = dto.getCuentaPadreId();
        this.descripcion = dto.getDescripcion();
        this.permiteMovimiento = dto.getPermiteMovimiento() != null ? dto.getPermiteMovimiento() : true;
        this.esCentroCosto = dto.getEsCentroCosto() != null ? dto.getEsCentroCosto() : false;
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
    }

    public CuentaContableDto toAggregate() {
        // Safely check if lazy proxy is initialized before accessing
        String padreCodigo = null;
        String padreNombre = null;
        if (cuentaPadre != null && Hibernate.isInitialized(cuentaPadre)) {
            padreCodigo = cuentaPadre.getCodigo();
            padreNombre = cuentaPadre.getNombre();
        }

        return CuentaContableDto.builder()
                .id(id)
                .codigo(codigo)
                .nombre(nombre)
                .tipo(tipo)
                .naturaleza(naturaleza)
                .nivel(nivel)
                .cuentaPadreId(cuentaPadreId)
                .cuentaPadreCodigo(padreCodigo)
                .cuentaPadreNombre(padreNombre)
                .descripcion(descripcion)
                .permiteMovimiento(permiteMovimiento)
                .esCentroCosto(esCentroCosto)
                .activo(activo)
                .build();
    }
}
