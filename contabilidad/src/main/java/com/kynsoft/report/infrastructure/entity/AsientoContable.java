package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.AsientoContableDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Asiento Contable generado automáticamente desde MovimientoStock.
 * El usuario NO crea asientos manualmente - son generados por el sistema.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "asiento_contable")
public class AsientoContable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "numero", unique = true, nullable = false, length = 30)
    private String numero;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "movimiento_stock_id")
    private UUID movimientoStockId;

    @Column(name = "tabla_origen", length = 50)
    private String tablaOrigen;

    @Column(name = "total_debe", precision = 15, scale = 2)
    private BigDecimal totalDebe;

    @Column(name = "total_haber", precision = 15, scale = 2)
    private BigDecimal totalHaber;

    @Column(name = "asentado")
    private Boolean asentado;

    @Column(name = "fecha_asentado")
    private LocalDateTime fechaAsentado;

    @Column(name = "usuario_asento", length = 100)
    private String usuarioAsento;

    @Column(name = "regla_id")
    private UUID reglaId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "asiento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LineaAsiento> lineas = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        if (asentado == null) {
            asentado = true;
        }
        if (tablaOrigen == null) {
            tablaOrigen = "movimiento_stock";
        }
        if (totalDebe == null) {
            totalDebe = BigDecimal.ZERO;
        }
        if (totalHaber == null) {
            totalHaber = BigDecimal.ZERO;
        }
    }

    public void addLinea(LineaAsiento linea) {
        lineas.add(linea);
        linea.setAsiento(this);
        recalcularTotales();
    }

    public void recalcularTotales() {
        this.totalDebe = lineas.stream()
                .map(l -> l.getDebe() != null ? l.getDebe() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalHaber = lineas.stream()
                .map(l -> l.getHaber() != null ? l.getHaber() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean estaCuadrado() {
        return totalDebe.compareTo(totalHaber) == 0;
    }

    public AsientoContableDto toAggregate() {
        return AsientoContableDto.builder()
                .id(id)
                .numero(numero)
                .fecha(fecha)
                .descripcion(descripcion)
                .movimientoStockId(movimientoStockId)
                .tablaOrigen(tablaOrigen)
                .totalDebe(totalDebe)
                .totalHaber(totalHaber)
                .asentado(asentado)
                .fechaAsentado(fechaAsentado)
                .usuarioAsento(usuarioAsento)
                .reglaId(reglaId)
                .lineas(lineas != null ?
                        lineas.stream().map(LineaAsiento::toAggregate).collect(Collectors.toList()) :
                        new ArrayList<>())
                .build();
    }
}
