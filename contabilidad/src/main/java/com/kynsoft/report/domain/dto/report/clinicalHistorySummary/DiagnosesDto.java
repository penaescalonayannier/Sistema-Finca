package com.kynsoft.report.domain.dto.report.clinicalHistorySummary;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class DiagnosesDto {
    private String code;
    private String diagnoses;
}
