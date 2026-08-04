package com.kynsoft.report.domain.dto.report.clinicalHistorySummary;

import com.kynsoft.report.domain.dto.report.confColor.Conf;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ReportClinicalHistorySummaryDto {
    private String logo;
    private String logo1;
    private String date;
    private String name;
    private String gender;
    private String age;
    private String registerNumber;
    private String prescriptionNumber;
    private String patientname;
    private String patientidentification;
    private String doctorname;
    private String specialty;
    private String insurancecompany;
    private String insurancedate;
    private String reason;
    private String symptoms;
    private String physicalexam;
    private String observations;
    private List<DiagnosesHistorySummaryDto> diagnoses;
    private List<MedicationHistorySummaryDto> medication;
    private Conf color;
    private String phones;
    private String email;
    private String website;
}
