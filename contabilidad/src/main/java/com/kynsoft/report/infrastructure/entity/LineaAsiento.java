package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.LineaAsientoDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Línea de Asiento Contable.
 * Representa una línea de débito o crédito dentro de un asiento.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "linea_asiento")
public class LineaAsiento {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asiento_id", nullable = false)
    private AsientoContable asiento;

    @Column(name = "codigo_cuenta", nullable = false, length = 20)
    private String codigoCuenta;

    @Column(name = "nombre_cuenta", length = 150)
    private String nombreCuenta;

    @Column(name = "centro_costo", length = 20)
    private String centroCosto;

    @Column(name = "debe", precision = 15, scale = 2)
    private BigDecimal debe;

    @Column(name = "haber", precision = 15, scale = 2)
    private BigDecimal haber;

    @Column(name = "concepto", length = 255)
    private String concepto;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (debe == null) {
            debe = BigDecimal.ZERO;
        }
        if (haber == null) {
            haber = BigDecimal.ZERO;
        }
        if (orden == null) {
            orden = 1;
        }
    }

    public static LineaAsiento crearDebito(String codigoCuenta, String nombreCuenta,
                                            BigDecimal monto, String concepto, String centroCosto) {
        LineaAsiento linea = new LineaAsiento();
        linea.setCodigoCuenta(codigoCuenta);
        linea.setNombreCuenta(nombreCuenta);
        linea.setDebe(monto);
        linea.setHaber(BigDecimal.ZERO);
        linea.setConcepto(concepto);
        linea.setCentroCosto(centroCosto);
        linea.setOrden(1);
        return linea;
    }

    public static LineaAsiento crearCredito(String codigoCuenta, String nombreCuenta,
                                             BigDecimal monto, String concepto, String centroCosto) {
        LineaAsiento linea = new LineaAsiento();
        linea.setCodigoCuenta(codigoCuenta);
        linea.setNombreCuenta(nombreCuenta);
        linea.setDebe(BigDecimal.ZERO);
        linea.setHaber(monto);
        linea.setConcepto(concepto);
        linea.setCentroCosto(centroCosto);
        linea.setOrden(2);
        return linea;
    }

    public LineaAsientoDto toAggregate() {
        return LineaAsientoDto.builder()
                .id(id)
                .asientoId(asiento != null ? asiento.getId() : null)
                .codigoCuenta(codigoCuenta)
                .nombreCuenta(nombreCuenta)
                .centroCosto(centroCosto)
                .debe(debe)
                .haber(haber)
                .concepto(concepto)
                .orden(orden)
                .build();
    }
}
