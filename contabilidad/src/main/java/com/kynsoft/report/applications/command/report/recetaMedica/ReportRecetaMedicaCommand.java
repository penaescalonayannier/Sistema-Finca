package com.kynsoft.report.applications.command.report.recetaMedica;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.domain.dto.report.clinicalHistorySummary.DiagnosesDto;
import com.kynsoft.report.domain.dto.report.clinicalHistorySummary.MedicationDto;
import com.kynsoft.report.domain.dto.report.confColor.Conf;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Getter
@Setter
@Builder
public class ReportRecetaMedicaCommand implements ICommand {

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

    private ByteArrayOutputStream baos;

    public static ReportRecetaMedicaCommand fromRequest(ReportRecetaMedicaRequest request) {
        return ReportRecetaMedicaCommand.builder()
                .logo(request.getLogo())
                .logo1(request.getLogo1())
                .name(request.getName())
                .registerNumber(request.getRegisterNumber())
                .prescriptionNumber(request.getPrescriptionNumber())
                .date(request.getDate())
                .patientName(request.getPatientName())
                .doctorName(request.getDoctorName())
                .patientId(request.getPatientId())
                .doctorSpecialty(request.getDoctorSpecialty())
                .gender(request.getGender())
                .age(request.getAge())
                .recordNumber(request.getRecordNumber())
                .phones(request.getPhones())
                .email(request.getEmail())
                .website(request.getWebsite())
                .medications(request.getMedications())
                .diagnoses(request.getDiagnoses())
                .color(request.getColor() != null ? request.getColor() : Conf.builder().build())
                .build();
    }

    @Override
    public ICommandMessage getMessage() {
        return new ReportRecetaMedicaCMessage(baos);
    }
}
