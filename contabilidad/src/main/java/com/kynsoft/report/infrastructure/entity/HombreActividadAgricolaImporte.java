package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.HombreActividadAgricolaImporteDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "hombre_actividad_agricola_importe")
public class HombreActividadAgricolaImporte {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id")
    private Trabajador trabajador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labor_id")
    private Labor labor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrumento_trabajo_id")
    private InstrumentoTrabajo instrumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloque_id")
    private Bloque bloque;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campo_id")
    private Campo campo;

    @Column(name = "dias", precision = 10, scale = 2)
    private BigDecimal dias;

    @Column(name = "cant_norma", precision = 10, scale = 2)
    private BigDecimal cantNorma;

    @Column(name = "cant_realizada", precision = 10, scale = 2)
    private BigDecimal cantRealizada;

    @Column(name = "horas", precision = 10, scale = 2)
    private BigDecimal horas;

    @Column(name = "norma", precision = 10, scale = 2)
    private BigDecimal norma;

    @Column(name = "tasa", precision = 10, scale = 2)
    private BigDecimal tasa;

    @Column(name = "importe", precision = 10, scale = 2)
    private BigDecimal importe;

    public HombreActividadAgricolaImporte(HombreActividadAgricolaImporteDto dto) {
        this.id = dto.getId();
        this.trabajador = dto.getTrabajador() != null ? new Trabajador(dto.getTrabajador()) : null;
        this.labor = dto.getLabor() != null ? new Labor(dto.getLabor()) : null;
        this.instrumento = dto.getInstrumento() != null ? new InstrumentoTrabajo(dto.getInstrumento()) : null;
        this.bloque = dto.getBloque() != null ? new Bloque(dto.getBloque()) : null;
        this.campo = dto.getCampo() != null ? new Campo(dto.getCampo()) : null;
        this.dias = dto.getDias();
        this.horas = dto.getHoras();
        this.norma = dto.getNorma();
        this.tasa = dto.getTasa();
        this.importe = dto.getImporte();
    }

    /**
     * Convierte la entidad a DTO
     * @return HombreActividadAgricolaImporteDto con los datos de la entidad
     */
    public HombreActividadAgricolaImporteDto toAggregate() {
        return HombreActividadAgricolaImporteDto.builder()
                .id(id)
                .trabajador(trabajador != null ? trabajador.toAggregate() : null)
                .labor(labor != null ? labor.toAggregate() : null)
                .instrumento(instrumento != null ? instrumento.toAggregate() : null)
                .bloque(bloque != null ? bloque.toAggregate() : null)
                .campo(campo != null ? campo.toAggregate() : null)
                .dias(dias)
                .horas(horas)
                .norma(norma)
                .tasa(tasa)
                .importe(importe)
                .build();
    }
}