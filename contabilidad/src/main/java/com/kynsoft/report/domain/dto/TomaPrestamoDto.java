package com.kynsoft.report.domain.dto;

import com.kynsoft.report.domain.dto.enumerativos.TipoTomaPrestamo;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import lombok.Builder;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class TomaPrestamoDto {

    private UUID id;
    private Double importe;
    private LocalDate fecha;
    private String cuentaDestino;
    private TipoTomaPrestamo tipo;
    private String observaciones;
    private String creditoId;

    //Para mantener la marca de lo utilizado en el tiempo.
    private Double importeUtilizadoEfectivo;
    private Double importeUtilizadoSuministros;
    private Double importeUtilizadoSeguro;
}
