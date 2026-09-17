package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.AlcanceFormaNumerada;
import com.kynsoft.report.domain.dto.FormaNumeradaDto;
import com.kynsoft.report.domain.dto.ModoEmisionFormaNumerada;
import com.kynsoft.report.domain.dto.ReinicioConsecutivoForma;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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
@Table(name = "forma_numerada")
public class FormaNumerada {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 60)
    private String codigo;

    @Column(nullable = false, length = 160)
    private String nombre;

    @Column(name = "referencia_modelo", length = 80)
    private String referenciaModelo;

    @Column(nullable = false, length = 30)
    private String prefijo;

    @Column(nullable = false)
    private Integer digitos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private ReinicioConsecutivoForma reinicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "alcance_predeterminado", nullable = false, length = 15)
    private AlcanceFormaNumerada alcancePredeterminado;

    @Enumerated(EnumType.STRING)
    @Column(name = "modo_emision", nullable = false, length = 15)
    private ModoEmisionFormaNumerada modoEmision;

    @Column(nullable = false)
    private Boolean activa = true;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

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
        if (activa == null) activa = true;
    }

    @PreUpdate
    void preUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

    public FormaNumeradaDto toDto() {
        return FormaNumeradaDto.builder()
                .id(id).codigo(codigo).nombre(nombre).referenciaModelo(referenciaModelo)
                .prefijo(prefijo).digitos(digitos).reinicio(reinicio)
                .alcancePredeterminado(alcancePredeterminado).modoEmision(modoEmision)
                .activa(activa).fechaInicio(fechaInicio).fechaFin(fechaFin)
                .creadoEn(creadoEn).actualizadoEn(actualizadoEn).build();
    }
}
