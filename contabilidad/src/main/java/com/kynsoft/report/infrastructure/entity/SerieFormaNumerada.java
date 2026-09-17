package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.AlcanceFormaNumerada;
import com.kynsoft.report.domain.dto.EstadoSerieFormaNumerada;
import com.kynsoft.report.domain.dto.SerieFormaNumeradaDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "serie_forma_numerada")
public class SerieFormaNumerada {

    @Id
    private UUID id;

    @Column(name = "forma_id", nullable = false)
    private UUID formaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "alcance_tipo", nullable = false, length = 15)
    private AlcanceFormaNumerada alcanceTipo;

    @Column(name = "alcance_id")
    private UUID alcanceId;

    /** Año de la serie; nulo cuando la forma está configurada como continua. */
    @Column(name = "anio")
    private Integer anio;

    @Column(length = 30)
    private String prefijo;

    @Column(name = "numero_inicial", nullable = false)
    private Integer numeroInicial = 1;

    @Column(name = "ultimo_numero", nullable = false)
    private Integer ultimoNumero = 0;

    @Column(name = "numero_final")
    private Integer numeroFinal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoSerieFormaNumerada estado = EstadoSerieFormaNumerada.ACTIVA;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();
        if (creadoEn == null) creadoEn = ahora;
        actualizadoEn = ahora;
        if (fechaInicio == null) fechaInicio = LocalDate.now();
        if (numeroInicial == null) numeroInicial = 1;
        if (ultimoNumero == null) ultimoNumero = numeroInicial - 1;
        if (estado == null) estado = EstadoSerieFormaNumerada.ACTIVA;
    }

    @PreUpdate
    void preUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

    public SerieFormaNumeradaDto toDto() {
        return SerieFormaNumeradaDto.builder()
                .id(id).formaId(formaId).alcanceTipo(alcanceTipo).alcanceId(alcanceId)
                .anio(anio).prefijo(prefijo).numeroInicial(numeroInicial).ultimoNumero(ultimoNumero)
                .numeroFinal(numeroFinal).estado(estado).fechaInicio(fechaInicio).fechaFin(fechaFin)
                .version(version).creadoEn(creadoEn).actualizadoEn(actualizadoEn).build();
    }
}
