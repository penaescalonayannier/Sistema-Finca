package com.kynsoft.report.domain.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class VariedadDto {
    private UUID id;
    private String code;
    private String name;
}
