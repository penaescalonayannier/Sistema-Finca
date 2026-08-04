package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.EstadoCuentaDto;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@AllArgsConstructor
public class EstadoCuenta {

    @Id
    @Column(name = "id")
    private UUID id;
    private LocalDate fecha;
    private String refOrigen;
    private String refCorriente;
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    private String tipo;
    private Double importe;

    private UUID clienteId;

    public EstadoCuenta(EstadoCuentaDto estadoCuenta) {
        this.id = estadoCuenta.getId();
        this.fecha = estadoCuenta.getFecha();
        this.refOrigen = estadoCuenta.getRefOrigen();
        this.refCorriente = estadoCuenta.getRefCorriente();
        this.observaciones = estadoCuenta.getObservaciones();
        this.importe = estadoCuenta.getImporte();
        this.tipo = estadoCuenta.getTipo();
        this.clienteId = estadoCuenta.getClienteId();
    }

    public EstadoCuentaDto toAggregate() {
        return EstadoCuentaDto
                .builder()
                .id(id)
                .fecha(fecha)
                .refCorriente(refCorriente)
                .refOrigen(refOrigen)
                .observaciones(observaciones)
                .importe(importe)
                .tipo(tipo)
                .clienteId(clienteId)
                .build();
    }
}
