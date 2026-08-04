package com.kynsoft.report.domain.dto.report.clinicalHistorySummary;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class DiagnosesHistorySummaryDto {
    private String cie;
    private String description;
}
