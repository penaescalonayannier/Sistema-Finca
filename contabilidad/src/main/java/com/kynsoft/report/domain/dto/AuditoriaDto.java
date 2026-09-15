package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class AuditoriaDto {

    private UUID id;
    private UUID usuarioId;
    private String username;
    private TipoAccion accion;
    private String entidad;
    private UUID entidadId;
    private String descripcion;
    private String valorAnterior;
    private String valorNuevo;
    private String ipAddress;
    private LocalDateTime createdAt;
}
