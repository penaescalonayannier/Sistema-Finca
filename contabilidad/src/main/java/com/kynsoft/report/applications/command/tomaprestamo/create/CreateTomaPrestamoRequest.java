package com.kynsoft.report.applications.command.tomaprestamo.create;

import com.kynsoft.report.domain.dto.enumerativos.TipoTomaPrestamo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTomaPrestamoRequest {
    private Double importe;
    private LocalDate fecha;
    private String cuentaDestino;
    private TipoTomaPrestamo tipo;
    private String observaciones;
    private String creditoId;
    private Double importeUtilizadoEfectivo;
    private Double importeUtilizadoSuministros;
    private Double importeUtilizadoSeguro;
}