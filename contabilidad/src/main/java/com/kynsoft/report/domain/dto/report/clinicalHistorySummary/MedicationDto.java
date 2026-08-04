package com.kynsoft.report.domain.dto.report.clinicalHistorySummary;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class MedicationDto {
    private String medication;
    private String presentation;
    private String quantity;
    private String instructions;
}
