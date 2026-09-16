package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArqueoCajaResumenDto {
    private UUID id;
    private Long numero;
    private UUID fincaId;
    private String fincaCodigo;
    private String fincaNombre;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private EstadoArqueoCaja estado;
    private TipoArqueoCaja tipo;
    private String contadorResponsable;
    private UUID contadorUsuarioId;
    private String custodio;
    private String recibidoPor;
    private String observaciones;
    private String observacionesApertura;
    private String observacionesCierre;
    private Double totalEsperado;
    private Double totalFisico;
    private Double diferencia;
    private Integer denominacionesRevisadas;
    private List<ArqueoCajaDenominacionDto> denominaciones;
}
