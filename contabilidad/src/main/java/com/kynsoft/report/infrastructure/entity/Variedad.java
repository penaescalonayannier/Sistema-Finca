package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.VariedadDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class Variedad {
    @Id
    @Column(name = "id")
    private UUID id;
    private String code;
    private String name;

    public Variedad(VariedadDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
    }

    public VariedadDto toAggregate() {
        return VariedadDto
                .builder()
                .id(id)
                .code(code)
                .name(name)
                .build();
    }
}
