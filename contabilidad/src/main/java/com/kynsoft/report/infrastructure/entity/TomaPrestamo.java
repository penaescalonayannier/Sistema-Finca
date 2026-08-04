package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.TomaPrestamoDto;
import com.kynsoft.report.domain.dto.enumerativos.TipoTomaPrestamo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class TomaPrestamo {

    @Id
    @Column(name = "id")
    private UUID id;
    private Double importe;
    private LocalDate fecha;
    private String cuentaDestino;
    private String tipo;
    private String observaciones;
    private String creditoId;

    //Para mantener la marca de lo utilizado en el tiempo.
    private Double importeUtilizadoEfectivo;
    private Double importeUtilizadoSuministros;
    private Double importeUtilizadoSeguro;

    public TomaPrestamo(TomaPrestamoDto dto) {
        this.id = dto.getId();
        this.importe = dto.getImporte();
        this.fecha = dto.getFecha();
        this.cuentaDestino = dto.getCuentaDestino();
        this.tipo = dto.getTipo().name();
        this.observaciones = dto.getObservaciones();
        this.creditoId = dto.getCreditoId();
        this.importeUtilizadoEfectivo = dto.getImporteUtilizadoEfectivo();
        this.importeUtilizadoSuministros = dto.getImporteUtilizadoSuministros();
        this.importeUtilizadoSeguro = dto.getImporteUtilizadoSeguro();
    }

    public TomaPrestamoDto toAggregate() {
        return TomaPrestamoDto
                .builder()
                .id(id)
                .importe(importe)
                .fecha(fecha)
                .cuentaDestino(cuentaDestino)
                .tipo(TipoTomaPrestamo.valueOf(tipo))
                .observaciones(observaciones)
                .creditoId(creditoId)
                .importeUtilizadoEfectivo(importeUtilizadoEfectivo)
                .importeUtilizadoSuministros(importeUtilizadoSuministros)
                .importeUtilizadoSeguro(importeUtilizadoSeguro)
                .build();
    }
}
