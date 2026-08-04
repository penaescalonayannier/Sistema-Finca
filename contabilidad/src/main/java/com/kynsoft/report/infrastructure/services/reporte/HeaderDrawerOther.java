package com.kynsoft.report.infrastructure.services.reporte;

import com.kynsoft.report.domain.services.IReportServicePdfBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class HeaderDrawerOther {

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
        float baseLineHeight = 8f;

        // === LÍNEA ENCIMA DEL TÍTULO - POSICIÓN MÁS ALTA ===
        this.reportService.drawLine(
                contentStream,
                margin,
                yPosition + baseLineHeight - 28,
                pageWidth - margin - 5,
                yPosition + baseLineHeight - 28,
                1f, 0f, 0f, 0f
        );

        // === DIBUJAR LOGO IZQUIERDO CON TAMAÑO MÁS COMPACTO ===
        float logoWidth = 0;
        if (headerData.getLeftLogo() != null && !headerData.getLeftLogo().isEmpty()) {
            this.reportService.drawBase64Image(
                    document,
                    contentStream,
                    headerData.getLeftLogo(),
                    margin,
                    yPosition - 18,
                    60,
                    40
            );
            logoWidth = 60;
        }

        // === TÍTULO CENTRADO ===
        this.reportService.drawMergedCellWithoutBorders(
                contentStream,
                headerData.getFontBold(),
                headerData.getReportTitle(),
                margin,
                yPosition,
                pageWidth - (2 * margin),
                baseLineHeight,
                com.kynsoft.report.domain.dto.status.TextAlignment.CENTER,
                false,
                headerData.getColor().getRed(),
                headerData.getColor().getGreen(),
                headerData.getColor().getBlue(),
                headerData.getColor().getFontSize() + 2f,
                1.5f
        );

        // === FECHA ALINEADA A LA DERECHA ===
        if (headerData.getDate() != null) {
            this.reportService.drawMergedCellWithoutBorders(
                    contentStream,
                    headerData.getFontBold(),
                    headerData.getDate(),
                    margin,
                    yPosition,
                    pageWidth - (2 * margin),
                    baseLineHeight,
                    com.kynsoft.report.domain.dto.status.TextAlignment.RIGHT,
                    false,
                    headerData.getColor().getRed(),
                    headerData.getColor().getGreen(),
                    headerData.getColor().getBlue(),
                    headerData.getColor().getFontSize() - 2,
                    1.0f
            );
        }

        yPosition -= baseLineHeight;

        // === NOMBRE DE LA CLÍNICA CENTRADO ===
        if (headerData.getClinicName() != null && !headerData.getClinicName().isEmpty()) {
            this.reportService.drawMergedCellWithoutBorders(
                    contentStream,
                    headerData.getFontBold(),
                    headerData.getClinicName(),
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
                    1.0f
            );
            yPosition -= baseLineHeight;
        }

        // === NÚMERO DE REGISTRO A LA DERECHA ===
        if (headerData.getPrescriptionNumber() != null) {
            this.reportService.drawMergedCellWithoutBorders(
                    contentStream,
                    headerData.getFontRegular(),
                    "No. " + headerData.getPrescriptionNumber(),
                    margin,
                    yPosition,
                    pageWidth - (2 * margin),
                    baseLineHeight,
                    com.kynsoft.report.domain.dto.status.TextAlignment.RIGHT,
                    false,
                    headerData.getColor().getRed(),
                    headerData.getColor().getGreen(),
                    headerData.getColor().getBlue(),
                    headerData.getColor().getFontSize() - 2,
                    1.0f
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

        // Fila 1: Paciente y Cédula - ALTURA FIJA COMPACTA
        if (headerData.getPatientName() != null) {
            float[] columnWidthsRow1 = {55, 190, 45, 105, 50, 65, 35, 25};
            String[] textsRow1 = {
                "Paciente:",
                headerData.getPatientName() != null ? this.reportService.capitalizarNombrePropio(headerData.getPatientName()) : "",
                "Cédula:",
                headerData.getPatientId() != null ? headerData.getPatientId() : "",
                "Género:",
                headerData.getGender() != null ? this.reportService.capitalizarNombrePropio(headerData.getGender()) : "",
                "Edad:",
                headerData.getAge() != null ? headerData.getAge() : ""
            };
            boolean[] coloredCellsRow1 = {false, false, false, false, false, false, false, false};
            boolean[] boldCellsRow1 = {true, false, true, false, true, false, true, false};

            // ALTURA FIJA en lugar de calcular
            float rowHeight1 = 18f;

            this.reportService.drawCustomRow(
                    contentStream,
                    currentX,
                    yPosition,
                    columnWidthsRow1,
                    rowHeight1,
                    textsRow1,
                    fontRegular,
                    fontBold,
                    11f,
                    false,
                    coloredCellsRow1, 
                    boldCellsRow1,
                    headerData.getColor().getRed(),
                    headerData.getColor().getGreen(),
                    headerData.getColor().getBlue(),
                    false
            );
            yPosition -= rowHeight1;
        }

        // Fila 2: Doctor y Especialidad - ALTURA FIJA COMPACTA
        if (headerData.getDoctorName() != null) {
            float[] columnWidthsRow2 = {45, 200, 80, 100, 55, 90};
            String[] textsRow2 = {
                "Doctor:",
                headerData.getDoctorName() != null ? this.reportService.capitalizarNombrePropio(headerData.getDoctorName()) : "",
                "Especialidad:",
                headerData.getDoctorSpecialty() != null ? this.reportService.capitalizarOracionesAvanzado(headerData.getDoctorSpecialty()) : "",
                "Registro:",
                headerData.getRecordNumber() != null ? this.reportService.capitalizarOracionesAvanzado(headerData.getRecordNumber()) : ""
            };
            boolean[] coloredCellsRow2 = {false, false, false, false, false, false};
            boolean[] boldCellsRow2 = {true, false, true, false, true, false};

            // ALTURA FIJA
            float rowHeight2 = 18f;

            this.reportService.drawCustomRow(
                    contentStream,
                    currentX,
                    yPosition,
                    columnWidthsRow2,
                    rowHeight2,
                    textsRow2,
                    fontRegular,
                    fontBold,
                    11f,
                    false,
                    coloredCellsRow2,
                    boldCellsRow2,
                    headerData.getColor().getRed(),
                    headerData.getColor().getGreen(),
                    headerData.getColor().getBlue(),
                    false
            );
            yPosition -= rowHeight2;
        }

        // Fila 3: Aseguradora - ALTURA FIJA COMPACTA
        if (headerData.isAseguradora()) {
            float[] columnWidthsRow3 = {100, 470};
            String[] textsRow3 = {
                "Aseguradora:",
                headerData.getAseguradora() != null ? this.reportService.capitalizarNombrePropio(headerData.getAseguradora()) : ""
            };
            boolean[] coloredCellsRow3 = {false, false};
            boolean[] boldCellsRow3 = {true, false};

            // ALTURA FIJA
            float rowHeight3 = 18f;

            this.reportService.drawCustomRow(
                    contentStream,
                    currentX,
                    yPosition,
                    columnWidthsRow3,
                    rowHeight3,
                    textsRow3,
                    fontRegular,
                    fontBold,
                    11f,
                    false,
                    coloredCellsRow3,
                    boldCellsRow3,
                    headerData.getColor().getRed(),
                    headerData.getColor().getGreen(),
                    headerData.getColor().getBlue(),
                    false
            );
            yPosition -= rowHeight3;
        }

        return yPosition;
    }
}