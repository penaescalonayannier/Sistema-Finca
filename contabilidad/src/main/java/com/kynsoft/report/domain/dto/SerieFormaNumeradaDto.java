package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SerieFormaNumeradaDto {
    private UUID id;
    private UUID formaId;
    private AlcanceFormaNumerada alcanceTipo;
    private UUID alcanceId;
    private Integer anio;
    private String prefijo;
    private Integer numeroInicial;
    private Integer ultimoNumero;
    private Integer numeroFinal;
    private EstadoSerieFormaNumerada estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long version;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
