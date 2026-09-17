package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/** Solicitud interna para reservar y registrar una forma oficial. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmitirFormaNumeradaRequest {
    private String codigoForma;
    private AlcanceFormaNumerada alcanceTipo;
    private UUID alcanceId;
    private LocalDate fechaDocumento;
    private String documentoTipo;
    private UUID documentoId;
    private UUID usuarioEmisorId;
}
