package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.UnidadMedidaDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Entity
public class UnidadMedida {

    @Id
    @Column(name = "id")
    private UUID id;
    private String code;
    private String name;

    public UnidadMedida(UnidadMedidaDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
    }

    public UnidadMedidaDto toAggregate() {
        return UnidadMedidaDto
                .builder()
                .id(id)
                .code(code)
                .name(name)
                .build();
    }
}
