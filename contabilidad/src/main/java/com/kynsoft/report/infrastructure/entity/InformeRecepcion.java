package com.kynsoft.report.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Expediente inmutable del Informe de recepción de almacén, SC-2-04. */
@Entity
@Getter
@Setter
@Table(name = "informe_recepcion")
public class InformeRecepcion {
    @Id
    private UUID id;
    @Column(name = "finca_id", nullable = false) private UUID fincaId;
    @Column(name = "almacen_id", nullable = false) private UUID almacenId;
    @Column(name = "numero_documento", nullable = false, length = 30) private String numeroDocumento;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_fuente", nullable = false, length = 30) private com.kynsoft.report.domain.dto.TipoMovimientoStock tipoFuente;
    @Column(name = "numero_fuente", nullable = false, length = 100) private String numeroFuente;
    @Column(name = "fecha_documento", nullable = false) private LocalDate fechaDocumento;
    @Column(name = "proveedor", nullable = false, length = 180) private String proveedor;
    @Column(name = "responsable_entrega", nullable = false, length = 180) private String responsableEntrega;
    @Column(name = "responsable_recibe", nullable = false, length = 180) private String responsableRecibe;
    @Column(name = "observaciones", length = 1000) private String observaciones;
    @Column(name = "estado", nullable = false, length = 20) private String estado;
    @Column(name = "movimiento_stock_id", nullable = false, unique = true) private UUID movimientoStockId;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;

    @OneToMany(mappedBy = "informeRecepcion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InformeRecepcionLinea> lineas = new ArrayList<>();

    @PrePersist void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (estado == null) estado = "REGISTRADO";
    }
    public void agregarLinea(InformeRecepcionLinea linea) { linea.setInformeRecepcion(this); lineas.add(linea); }
}
