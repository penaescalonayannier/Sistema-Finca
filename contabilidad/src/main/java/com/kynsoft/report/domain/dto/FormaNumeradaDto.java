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
public class FormaNumeradaDto {
    private UUID id;
    private String codigo;
    private String nombre;
    private String referenciaModelo;
    private String prefijo;
    private Integer digitos;
    private ReinicioConsecutivoForma reinicio;
    private AlcanceFormaNumerada alcancePredeterminado;
    private ModoEmisionFormaNumerada modoEmision;
    private Boolean activa;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
