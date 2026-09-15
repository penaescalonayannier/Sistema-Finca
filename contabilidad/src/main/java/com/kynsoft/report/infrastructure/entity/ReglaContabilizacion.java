package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.ReglaContabilizacionDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Regla de Contabilización.
 * Mapea TipoMovimientoStock → Cuentas Débito/Crédito automáticamente.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "regla_contabilizacion")
public class ReglaContabilizacion {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 50)
    private TipoMovimientoStock tipoMovimiento;

    @Column(name = "almacen_id")
    private UUID almacenId;

    @Column(name = "finca_id")
    private UUID fincaId;

    @Column(name = "tipo_producto", length = 50)
    private String tipoProducto;

    @Column(name = "cuenta_debito", nullable = false, length = 20)
    private String cuentaDebito;

    @Column(name = "cuenta_credito", nullable = false, length = 20)
    private String cuentaCredito;

    @Column(name = "centro_costo_debito", length = 20)
    private String centroCostoDebito;

    @Column(name = "centro_costo_credito", length = 20)
    private String centroCostoCredito;

    @Column(name = "descripcion_plantilla", length = 255)
    private String descripcionPlantilla;

    @Column(name = "prioridad")
    private Integer prioridad;

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
        if (prioridad == null) {
            prioridad = 100;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public ReglaContabilizacion(ReglaContabilizacionDto dto) {
        this.id = dto.getId() != null ? dto.getId() : UUID.randomUUID();
        this.tipoMovimiento = dto.getTipoMovimiento();
        this.almacenId = dto.getAlmacenId();
        this.fincaId = dto.getFincaId();
        this.tipoProducto = dto.getTipoProducto();
        this.cuentaDebito = dto.getCuentaDebito();
        this.cuentaCredito = dto.getCuentaCredito();
        this.centroCostoDebito = dto.getCentroCostoDebito();
        this.centroCostoCredito = dto.getCentroCostoCredito();
        this.descripcionPlantilla = dto.getDescripcionPlantilla();
        this.prioridad = dto.getPrioridad() != null ? dto.getPrioridad() : 100;
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
    }

    public ReglaContabilizacionDto toAggregate() {
        return ReglaContabilizacionDto.builder()
                .id(id)
                .tipoMovimiento(tipoMovimiento)
                .almacenId(almacenId)
                .fincaId(fincaId)
                .tipoProducto(tipoProducto)
                .cuentaDebito(cuentaDebito)
                .cuentaCredito(cuentaCredito)
                .centroCostoDebito(centroCostoDebito)
                .centroCostoCredito(centroCostoCredito)
                .descripcionPlantilla(descripcionPlantilla)
                .prioridad(prioridad)
                .activo(activo)
                .build();
    }
}
