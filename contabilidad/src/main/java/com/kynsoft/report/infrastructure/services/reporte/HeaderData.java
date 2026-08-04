package com.kynsoft.report.infrastructure.services.reporte;

import com.kynsoft.report.domain.dto.status.TextAlignment;
import com.kynsoft.report.domain.dto.report.confColor.Conf;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeaderData {
    // Logos
    private String leftLogo;
    private String rightLogo;
    
    // Título del reporte
    private String reportTitle;
    private TextAlignment titleAlignment;
    
    // Fecha
    private String date;
    private TextAlignment dateAlignment;
    
    // Información de la clínica
    private String clinicName;
    
    // Información del paciente
    private String patientName;
    private String patientId;
    private String gender;
    private String age;
    private String recordNumber;
    private String prescriptionNumber;
    
    // Información del doctor
    private String doctorName;
    private String doctorSpecialty;
    
    // Configuración de estilo
    private Conf color;
    private PDType1Font fontBold;
    private PDType1Font fontRegular;
    
    // Espacios adicionales
    @Builder.Default
    private int additionalSpaces = 1;
    @Builder.Default
    private boolean isAseguradora = false;
    private String aseguradora;
}