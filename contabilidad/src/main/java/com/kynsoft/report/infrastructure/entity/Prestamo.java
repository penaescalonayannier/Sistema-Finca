package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.PrestamoDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class Prestamo {

    @Id
    @Column(name = "id")
    private UUID id;
    private Double importeAprobado;
    private Double importeAprobadoEfectivo;
    private Double importeUtilizadoEfectivo;
    private Double importeAprobadoSuministros;
    private Double importeUtilizadoSuministros;
    private Double importeAprobadoSeguro;
    private Double importeUtilizadoSeguro;
    private String numeroContrato;
    private String cuenta;
    private String toneladasMolibles;
    private String observaciones;

    public Prestamo(PrestamoDto dto) {
        this.id = dto.getId();
        this.importeAprobado = dto.getImporteAprobado();
        this.importeAprobadoEfectivo = dto.getImporteAprobadoEfectivo();
        this.importeUtilizadoEfectivo = dto.getImporteUtilizadoEfectivo();
        this.importeAprobadoSuministros = dto.getImporteAprobadoSuministros();
        this.importeUtilizadoSuministros = dto.getImporteUtilizadoSuministros();
        this.importeAprobadoSeguro = dto.getImporteAprobadoSeguro();
        this.importeUtilizadoSeguro = dto.getImporteUtilizadoSeguro();
        this.numeroContrato = dto.getNumeroContrato();
        this.cuenta = dto.getCuenta();
        this.toneladasMolibles = dto.getToneladasMolibles();
        this.observaciones = dto.getObservaciones();
    }

    public PrestamoDto toAggregate() {
        return PrestamoDto
                .builder()
                .id(id)
                .importeAprobado(importeAprobado)
                .importeAprobadoEfectivo(importeAprobadoEfectivo)
                .importeUtilizadoEfectivo(importeUtilizadoEfectivo)
                .importeAprobadoSuministros(importeAprobadoSuministros)
                .importeUtilizadoSuministros(importeUtilizadoSuministros)
                .importeAprobadoSeguro(importeAprobadoSeguro)
                .importeUtilizadoSeguro(importeUtilizadoSeguro)
                .numeroContrato(numeroContrato)
                .cuenta(cuenta)
                .observaciones(observaciones)
                .toneladasMolibles(toneladasMolibles)
                .build();
    }
}
