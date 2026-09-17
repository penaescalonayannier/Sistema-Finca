package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmisionFormaNumeradaDto {
    private UUID id;
    private UUID formaId;
    private UUID serieId;
    private Integer numero;
    private String numeroFormateado;
    private String documentoTipo;
    private UUID documentoId;
    private EstadoEmisionFormaNumerada estado;
    private LocalDateTime fechaEmision;
    private UUID usuarioEmisorId;
    private LocalDateTime fechaAnulacion;
    private UUID usuarioAnulacionId;
    private String motivoAnulacion;
    private Integer totalReimpresiones;
    private LocalDateTime ultimaReimpresionEn;
    private UUID ultimoUsuarioReimpresionId;
}
