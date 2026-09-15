package com.kynsoft.report.domain.dto.reportes;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class FincaInfoDto {
    private UUID id;
    private String code;
    private String name;
}
