package com.kynsoft.report.domain.dto;

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
public class EstadoCuentaDto{

    private UUID id;
    private LocalDate fecha;
    private String refOrigen;
    private String refCorriente;
    private String observaciones;
    private String tipo;
    private Double importe;
    private UUID clienteId;
}
