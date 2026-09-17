package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.EmisionFormaNumeradaDto;
import com.kynsoft.report.domain.dto.EstadoEmisionFormaNumerada;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "emision_forma_numerada")
public class EmisionFormaNumerada {

    @Id
    private UUID id;

    @Column(name = "forma_id", nullable = false)
    private UUID formaId;

    @Column(name = "serie_id", nullable = false)
    private UUID serieId;

    @Column(nullable = false)
    private Integer numero;

    @Column(name = "numero_formateado", nullable = false, length = 80)
    private String numeroFormateado;

    @Column(name = "documento_tipo", nullable = false, length = 80)
    private String documentoTipo;

    @Column(name = "documento_id", nullable = false)
    private UUID documentoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoEmisionFormaNumerada estado = EstadoEmisionFormaNumerada.EMITIDA;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "usuario_emisor_id")
    private UUID usuarioEmisorId;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    @Column(name = "usuario_anulacion_id")
    private UUID usuarioAnulacionId;

    @Column(name = "motivo_anulacion", length = 1000)
    private String motivoAnulacion;

    @Column(name = "total_reimpresiones", nullable = false)
    private Integer totalReimpresiones = 0;

    @Column(name = "ultima_reimpresion_en")
    private LocalDateTime ultimaReimpresionEn;

    @Column(name = "ultimo_usuario_reimpresion_id")
    private UUID ultimoUsuarioReimpresionId;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (fechaEmision == null) fechaEmision = LocalDateTime.now();
        if (estado == null) estado = EstadoEmisionFormaNumerada.EMITIDA;
        if (totalReimpresiones == null) totalReimpresiones = 0;
    }

    public EmisionFormaNumeradaDto toDto() {
        return EmisionFormaNumeradaDto.builder()
                .id(id).formaId(formaId).serieId(serieId).numero(numero)
                .numeroFormateado(numeroFormateado).documentoTipo(documentoTipo)
                .documentoId(documentoId).estado(estado).fechaEmision(fechaEmision)
                .usuarioEmisorId(usuarioEmisorId).fechaAnulacion(fechaAnulacion)
                .usuarioAnulacionId(usuarioAnulacionId).motivoAnulacion(motivoAnulacion)
                .totalReimpresiones(totalReimpresiones).ultimaReimpresionEn(ultimaReimpresionEn)
                .ultimoUsuarioReimpresionId(ultimoUsuarioReimpresionId).build();
    }
}
