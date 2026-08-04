package com.kynsoft.report.infrastructure.services.reporte;

import com.kynsoft.report.domain.services.IReportServicePdfBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class HeaderDrawer {
    
    private final IReportServicePdfBox reportService;
    
    public float drawHeader(
            PDDocument document,
            PDPageContentStream contentStream,
            HeaderData headerData,
            float startY,
            float pageWidth,
            float pageHeight,
            float margin
    ) throws Exception {
        
        float yPosition = startY;
        float baseLineHeight = 14f;
        
        // === LÍNEA ENCIMA DEL TÍTULO - PRIMERO ===
        this.reportService.drawLine(
            contentStream, 
            margin, 
            yPosition + baseLineHeight + 20, 
            pageWidth - margin - 5, 
            yPosition + baseLineHeight + 20, 
            1f, 0f, 0f, 0f
        );

        // === DIBUJAR LOGOS A LA MISMA ALTURA DEL TÍTULO ===
        // Logo izquierdo - alineado con el título
        if (headerData.getLeftLogo() != null && !headerData.getLeftLogo().isEmpty()) {
            this.reportService.drawBase64Image(
                document, 
                contentStream, 
                headerData.getLeftLogo(),
                margin, // x: margen izquierdo
                yPosition - 20, // y: alineado con el título (ajuste fino)
                70, // width: tamaño reducido
                50  // height: tamaño reducido
            );
        }

        // Logo derecho - alineado con el título
        if (headerData.getRightLogo() != null && !headerData.getRightLogo().isEmpty()) {
            this.reportService.drawBase64Image(
                document, 
                contentStream, 
                headerData.getRightLogo(),
                pageWidth - margin - 75, // x: margen derecho
                yPosition - 20, // y: alineado con el título (ajuste fino)
                70, // width: tamaño reducido
                50  // height: tamaño reducido
            );
        }

        // === TÍTULO DEL REPORTE ===
        this.reportService.drawMergedCellWithoutBorders(
                contentStream,
                headerData.getFontBold(),
                headerData.getReportTitle(),
                margin,
                yPosition,
                pageWidth - (2 * margin),
                baseLineHeight,
                headerData.getTitleAlignment(),
                false,
                headerData.getColor().getRed(),
                headerData.getColor().getGreen(),
                headerData.getColor().getBlue(),
                headerData.getColor().getFontSize(),
                0.3f
        );
        yPosition -= baseLineHeight;
//
//        // === FECHA (OPCIONAL) ===
//        if (headerData.getDate() != null && !headerData.getDate().isEmpty()) {
//            this.reportService.drawMergedCellWithoutBorders(
//                    contentStream,
//                    headerData.getFontRegular(),
//                    headerData.getDate(),
//                    margin,
//                    yPosition,
//                    pageWidth - (2 * margin),
//                    baseLineHeight,
//                    headerData.getDateAlignment(),
//                    false,
//                    headerData.getColor().getRed(),
//                    headerData.getColor().getGreen(),
//                    headerData.getColor().getBlue(),
//                    headerData.getColor().getFontSize(),
//                    0.5f
//            );
//            yPosition -= baseLineHeight;
//        }

        // === REDUCIR ESPACIOS ADICIONALES ===
        for (int i = 0; i < headerData.getAdditionalSpaces(); i++) {
            this.reportService.drawMergedCellWithoutBorders(
                    contentStream,
                    headerData.getFontBold(),
                    "",
                    margin,
                    yPosition,
                    pageWidth - (2 * margin),
                    baseLineHeight,
                    com.kynsoft.report.domain.dto.status.TextAlignment.CENTER,
                    false,
                    headerData.getColor().getRed(),
                    headerData.getColor().getGreen(),
                    headerData.getColor().getBlue(),
                    headerData.getColor().getFontSize(),
                    0.3f
            );
            yPosition -= baseLineHeight;
        }

        // === INFORMACIÓN DE LA CLÍNICA Y PACIENTE ===
        yPosition = drawClinicAndPatientInfo(contentStream, headerData, margin, yPosition);
        
        return yPosition;
    }
    
    private float drawClinicAndPatientInfo(
            PDPageContentStream contentStream,
            HeaderData headerData,
            float currentX,
            float startY
    ) throws Exception {
        
        float yPosition = startY;
        PDType1Font fontBold = headerData.getFontBold();
        PDType1Font fontRegular = headerData.getFontRegular();
        
        // Fila 1: Clínica y Fecha
        if (headerData.getClinicName() != null) {
            float[] columnWidthsRow0 = {80, 240, 105, 145};
            String[] textsRow0 = {
                "CLÍNICA", 
                headerData.getClinicName(), 
                "FECHA", 
                headerData.getDate() != null ? headerData.getDate() : ""
            };
            boolean[] coloredCellsRow0 = {true, false, true, false};
            
            float rowHeight0 = this.reportService.calculateRowHeightPerson(
                    textsRow0, columnWidthsRow0, fontRegular, headerData.getColor().getFontSize()
            );
            
            this.reportService.drawCustomRow(
                    contentStream, currentX, yPosition, columnWidthsRow0, rowHeight0, textsRow0,
                    fontRegular, fontBold, headerData.getColor().getFontSize(), false,
                    coloredCellsRow0, coloredCellsRow0,
                    headerData.getColor().getRed(), headerData.getColor().getGreen(), headerData.getColor().getBlue()
            );
            yPosition -= rowHeight0;
        }

        // Fila 2: Paciente y Cédula
        if (headerData.getPatientName() != null) {
            float[] columnWidthsRow1 = {80, 240, 105, 145};
            String[] textsRow1 = {
                "PACIENTE", 
                headerData.getPatientName() != null ? this.reportService.capitalizarOracionesAvanzado(headerData.getPatientName()) : "",
                //headerData.getPatientName(),
                "CÉDULA", 
                headerData.getPatientId() != null ? headerData.getPatientId() : ""
            };
            boolean[] coloredCellsRow1 = {true, false, true, false};
            
            float rowHeight1 = this.reportService.calculateRowHeightPerson(
                    textsRow1, columnWidthsRow1, fontRegular, headerData.getColor().getFontSize()
            );
            
            this.reportService.drawCustomRow(
                    contentStream, currentX, yPosition, columnWidthsRow1, rowHeight1, textsRow1,
                    fontRegular, fontBold, headerData.getColor().getFontSize(), false,
                    coloredCellsRow1, coloredCellsRow1,
                    headerData.getColor().getRed(), headerData.getColor().getGreen(), headerData.getColor().getBlue()
            );
            yPosition -= rowHeight1;
        }

        // Fila 3: Doctor y Especialidad
        if (headerData.getDoctorName() != null) {
            float[] columnWidthsRow2 = {80, 240, 105, 145};
            String[] textsRow2 = {
                "DOCTOR", 
                //headerData.getDoctorName() != null ? headerData.getDoctorName() : "", 
                headerData.getDoctorName() != null ? this.reportService.capitalizarOracionesAvanzado(headerData.getDoctorName()) : "", 
                "ESPECIALIDAD", 
                //headerData.getDoctorSpecialty() != null ? headerData.getDoctorSpecialty() : ""
                headerData.getDoctorSpecialty() != null ? this.reportService.capitalizarOracionesAvanzado(headerData.getDoctorSpecialty()) : ""
            };
            boolean[] coloredCellsRow2 = {true, false, true, false};
            
            float rowHeight2 = this.reportService.calculateRowHeightPerson(
                    textsRow2, columnWidthsRow2, fontRegular, headerData.getColor().getFontSize()
            );
            
            this.reportService.drawCustomRow(
                    contentStream, currentX, yPosition, columnWidthsRow2, rowHeight2, textsRow2,
                    fontRegular, fontBold, headerData.getColor().getFontSize(), false,
                    coloredCellsRow2, coloredCellsRow2,
                    headerData.getColor().getRed(), headerData.getColor().getGreen(), headerData.getColor().getBlue()
            );
            yPosition -= rowHeight2;
        }

        // Fila 4: Información adicional (Género, Edad, Registro)
        if (headerData.getGender() != null || headerData.getAge() != null || headerData.getRecordNumber() != null) {
            float[] columnWidthsRow3 = {80, 90, 100, 50, 105, 145};
            String[] textsRow3 = {
                "GÉNERO", headerData.getGender() != null ? this.reportService.capitalizarOracionesAvanzado(headerData.getGender()) : "",
                "EDAD", headerData.getAge() != null ? headerData.getAge() : "",
                "NO. REGISTRO", headerData.getRecordNumber() != null ? headerData.getRecordNumber() : ""
            };
            boolean[] coloredCellsRow3 = {true, false, true, false, true, false};
            
            float rowHeight3 = this.reportService.calculateRowHeightPerson(
                    textsRow3, columnWidthsRow3, fontRegular, headerData.getColor().getFontSize()
            );
            
            this.reportService.drawCustomRow(
                    contentStream, currentX, yPosition, columnWidthsRow3, rowHeight3, textsRow3,
                    fontRegular, fontBold, headerData.getColor().getFontSize(), false,
                    coloredCellsRow3, coloredCellsRow3,
                    headerData.getColor().getRed(), headerData.getColor().getGreen(), headerData.getColor().getBlue()
            );
            yPosition -= rowHeight3;
        }
        
        return yPosition;
    }
}