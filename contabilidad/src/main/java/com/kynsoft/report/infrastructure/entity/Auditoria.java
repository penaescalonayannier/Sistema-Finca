package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.AuditoriaDto;
import com.kynsoft.report.domain.dto.TipoAccion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "auditoria", indexes = {
    @Index(name = "idx_auditoria_usuario", columnList = "usuario_id"),
    @Index(name = "idx_auditoria_entidad", columnList = "entidad"),
    @Index(name = "idx_auditoria_created_at", columnList = "created_at"),
    @Index(name = "idx_auditoria_accion", columnList = "accion")
})
public class Auditoria {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "username", length = 50)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "accion", nullable = false, length = 30)
    private TipoAccion accion;

    @Column(name = "entidad", nullable = false, length = 50)
    private String entidad;

    @Column(name = "entidad_id")
    private UUID entidadId;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_nuevo", columnDefinition = "TEXT")
    private String valorNuevo;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Auditoria(AuditoriaDto dto) {
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
        this.createdAt = dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now();
    }

    public AuditoriaDto toAggregate() {
        return AuditoriaDto.builder()
                .id(id)
                .usuarioId(usuarioId)
                .username(username)
                .accion(accion)
                .entidad(entidad)
                .entidadId(entidadId)
                .descripcion(descripcion)
                .valorAnterior(valorAnterior)
                .valorNuevo(valorNuevo)
                .ipAddress(ipAddress)
                .createdAt(createdAt)
                .build();
    }
}
