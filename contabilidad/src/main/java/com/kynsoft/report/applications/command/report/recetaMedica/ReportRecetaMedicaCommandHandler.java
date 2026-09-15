package com.kynsoft.report.applications.command.report.recetaMedica;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.report.clinicalHistorySummary.MedicalPrescriptionDto;
import com.kynsoft.report.infrastructure.services.reporte.ReportRecetaMedicaServiceImplNueva;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReportRecetaMedicaCommandHandler implements ICommandHandler<ReportRecetaMedicaCommand> {

    private final ReportRecetaMedicaServiceImplNueva recetaMedicaServiceImpl;

    public ReportRecetaMedicaCommandHandler(ReportRecetaMedicaServiceImplNueva recetaMedicaServiceImpl) {
        this.recetaMedicaServiceImpl = recetaMedicaServiceImpl;
    }

    @Override
    public void handle(ReportRecetaMedicaCommand command) {
        log.info("Creando el reporte Receta Medica");

        command.setBaos(this.recetaMedicaServiceImpl.constructionReport(
                MedicalPrescriptionDto
                        .builder()
                        .logo(command.getLogo())
                        .logo1(command.getLogo1())
                        .name(command.getName())
                        .registerNumber(command.getRegisterNumber())
                        .prescriptionNumber(command.getPrescriptionNumber())
                        .date(command.getDate())
                        .patientName(command.getPatientName())
                        .doctorName(command.getDoctorName())
                        .patientId(command.getPatientId())
                        .doctorSpecialty(command.getDoctorSpecialty())
                        .gender(command.getGender())
                        .age(command.getAge())
                        .recordNumber(command.getRecordNumber())
                        .phones(command.getPhones())
                        .email(command.getEmail())
                        .website(command.getWebsite())
                        .medications(command.getMedications())
                        .diagnoses(command.getDiagnoses())
                        .color(command.getColor())
                        .build()
        ));
        log.info("Creado el reporte Receta Medica");
    }

}
