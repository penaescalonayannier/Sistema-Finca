package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.FincaDto;
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
@Table(name = "fincas")
public class Finca {
    @Id
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 500)
    private String description;

    public Finca(FincaDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
    }

    public FincaDto toAggregate() {
        return FincaDto
                .builder()
                .id(id)
                .code(code)
                .name(name)
                .description(description)
                .build();
    }
}