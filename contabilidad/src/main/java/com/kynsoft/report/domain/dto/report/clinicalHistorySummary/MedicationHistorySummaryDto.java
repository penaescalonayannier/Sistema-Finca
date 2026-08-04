package com.kynsoft.report.domain.dto.report.clinicalHistorySummary;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class MedicationHistorySummaryDto {
    private String medication;
    private String instructions;
}
