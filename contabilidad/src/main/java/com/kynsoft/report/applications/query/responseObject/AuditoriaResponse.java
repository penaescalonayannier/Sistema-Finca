package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.AuditoriaDto;
import com.kynsoft.report.domain.dto.TipoAccion;
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
public class AuditoriaResponse implements IResponse {

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

    public AuditoriaResponse(AuditoriaDto dto) {
        this.id = dto.getId();
        this.usuarioId = dto.getUsuarioId();
        this.username = dto.getUsername();
        this.accion = dto.getAccion();
        this.entidad = dto.getEntidad();
        this.entidadId = dto.getEntidadId();
        this.descripcion = dto.getDescripcion();
        this.valorAnterior = dto.getValorAnterior();
        this.valorNuevo = dto.getValorNuevo();
        this.ipAddress = dto.getIpAddress();
        this.createdAt = dto.getCreatedAt();
    }
}
