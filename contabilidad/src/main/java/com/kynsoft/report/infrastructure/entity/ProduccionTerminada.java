package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.ProduccionTerminadaDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "produccion_terminada")
public class ProduccionTerminada {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "fecha")
    private String fecha;

    @Column(name = "recibido_por")
    private String recibidoPor;

    @Column(name = "entregado_por")
    private String entregadoPor;

    @Column(name = "consecutivo")
    private String consecutivo;

    public ProduccionTerminada(ProduccionTerminadaDto dto) {
        this.id = dto.getId();
        this.fecha = dto.getFecha();
        this.recibidoPor = dto.getRecibidoPor();
        this.entregadoPor = dto.getEntregadoPor();
        this.consecutivo = dto.getConsecutivo();
    }

    public ProduccionTerminadaDto toAggregate() {
        return ProduccionTerminadaDto
                .builder()
                .id(id)
                .fecha(fecha)
                .recibidoPor(recibidoPor)
                .entregadoPor(entregadoPor)
                .consecutivo(consecutivo)
                .build();
    }
}
