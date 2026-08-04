package com.kynsoft.report.applications.command.report.recetaMedica;

import com.kynsoft.report.domain.dto.report.clinicalHistorySummary.DiagnosesDto;
import com.kynsoft.report.domain.dto.report.clinicalHistorySummary.MedicationDto;
import com.kynsoft.report.domain.dto.report.confColor.Conf;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ReportRecetaMedicaRequest {

    private String logo;
    private String logo1;
    private String name;
    private String registerNumber;
    private String prescriptionNumber;
    private String date;
    private String patientName;
    private String doctorName;
    private String patientId;//Cedula
    private String doctorSpecialty;
    private String gender;
    private String age;
    private String recordNumber;
    private String phones;
    private String email;
    private String website;
    private List<MedicationDto> medications;
    private List<DiagnosesDto> diagnoses;
    private Conf color;
}
