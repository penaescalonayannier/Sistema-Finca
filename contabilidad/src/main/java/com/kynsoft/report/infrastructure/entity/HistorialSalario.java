package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.HistorialSalarioDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Registro inmutable de las condiciones salariales pactadas con un trabajador. */
@Entity
@Table(name = "historial_salario_trabajador")
@Getter
@Setter
@NoArgsConstructor
public class HistorialSalario {
    @Id
    private UUID id;
    @Column(name = "trabajador_id", nullable = false)
    private UUID trabajadorId;
    @Column(name = "finca_id", nullable = false)
    private UUID fincaId;
    @Column(name = "cargo_id")
    private UUID cargoId;
    @Column(name = "fecha_vigencia", nullable = false)
    private LocalDate fechaVigencia;
    @Column(name = "salario_escala", nullable = false, precision = 19, scale = 4)
    private BigDecimal salarioEscala;
    @Column(name = "anticipo_diario", nullable = false, precision = 19, scale = 4)
    private BigDecimal anticipoDiario;
    @Column(name = "tasa", nullable = false, precision = 19, scale = 4)
    private BigDecimal tasa;
    @Column(nullable = false, length = 500)
    private String motivo;
    @Column(nullable = false, length = 20)
    private String estado;
    @Column(name = "autorizado_por_id")
    private UUID autorizadoPorId;

    public HistorialSalario(HistorialSalarioDto dto) {
        this.id = dto.getId();
        this.trabajadorId = dto.getTrabajadorId();
        this.fincaId = dto.getFincaId();
        this.cargoId = dto.getCargoId();
        this.fechaVigencia = dto.getFechaVigencia();
        this.salarioEscala = dto.getSalarioEscala();
        this.anticipoDiario = dto.getAnticipoDiario();
        this.tasa = dto.getTasa();
        this.motivo = dto.getMotivo();
        this.estado = dto.getEstado();
        this.autorizadoPorId = dto.getAutorizadoPorId();
    }

    public HistorialSalarioDto toAggregate() {
        return HistorialSalarioDto.builder().id(id).trabajadorId(trabajadorId).fincaId(fincaId)
                .cargoId(cargoId).fechaVigencia(fechaVigencia).salarioEscala(salarioEscala)
                .anticipoDiario(anticipoDiario).tasa(tasa).motivo(motivo).estado(estado)
                .autorizadoPorId(autorizadoPorId).build();
    }
}
