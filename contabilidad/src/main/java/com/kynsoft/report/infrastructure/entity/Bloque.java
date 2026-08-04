package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.BloqueDto;
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
@Table(name = "bloque")
public class Bloque {
    @Id
    @Column(name = "id")
    private UUID id;
    private String code;
    private String name;

    public Bloque(BloqueDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
    }

    public BloqueDto toAggregate() {
       return BloqueDto
               .builder()
               .id(id)
               .code(code)
               .name(name)
               .build();
    }
}
