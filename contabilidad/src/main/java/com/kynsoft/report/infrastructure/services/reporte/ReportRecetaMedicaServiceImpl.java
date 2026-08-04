package com.kynsoft.report.infrastructure.services.reporte;

import com.kynsoft.report.domain.dto.report.clinicalHistorySummary.DiagnosesDto;
import com.kynsoft.report.domain.dto.report.clinicalHistorySummary.MedicalPrescriptionDto;
import com.kynsoft.report.domain.dto.report.clinicalHistorySummary.MedicationDto;
import com.kynsoft.report.domain.dto.status.TextAlignment;
import com.kynsoft.report.domain.services.IReportServicePdfBox;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class ReportRecetaMedicaServiceImpl {

    private final IReportServicePdfBox reportServiceImpl;
    private final HeaderDrawerOther headerDrawer;
    private final ContactDrawer contactDrawer;

    public ByteArrayOutputStream constructionReport(MedicalPrescriptionDto medicalPrescription) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PDDocument document = new PDDocument();

        try {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            float margin = 10;
            float yPosition = pageHeight - margin - 30; // BAJAMOS la posición inicial 30 puntos
            float baseLineHeight = 14f;
            float baseLineSalto = 10f;

            float[] baseColumnWidths = {100, 70, 120, 170, 110};
            float totalBaseWidth = this.reportServiceImpl.getTotalWidth(baseColumnWidths, 0, baseColumnWidths.length);
            float currentX = margin;

            HeaderData headerData = HeaderData.builder()
                    .leftLogo(medicalPrescription.getLogo())
                    .rightLogo(medicalPrescription.getLogo1())
                    .reportTitle("RECETA MÉDICA")
                    .titleAlignment(TextAlignment.CENTER)
                    .date(medicalPrescription.getDate())
                    .dateAlignment(TextAlignment.RIGHT)
                    .clinicName(medicalPrescription.getName())
                    .patientName(medicalPrescription.getPatientName())
                    .patientId(medicalPrescription.getPatientId())
                    .gender(medicalPrescription.getGender())
                    .age(medicalPrescription.getAge())
                    .recordNumber(medicalPrescription.getRecordNumber())
                    .prescriptionNumber(medicalPrescription.getPrescriptionNumber())
                    .doctorName(medicalPrescription.getDoctorName())
                    .doctorSpecialty(medicalPrescription.getDoctorSpecialty())
                    .color(medicalPrescription.getColor())
                    .fontBold(fontBold)
                    .fontRegular(fontRegular)
                    .additionalSpaces(1) // REDUCIDO de 2 a 1
                    .build();
            try {
                float initialY = pageHeight - margin - 50; // Ajustado para mejor alineación
                yPosition = headerDrawer.drawHeader(document, contentStream, headerData, initialY, pageWidth, pageHeight, margin);
            } catch (Exception ex) {
                Logger.getLogger(ReportRecetaMedicaServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            this.reportServiceImpl.drawLine(
                    contentStream,
                    margin,
                    yPosition + baseLineHeight - 15, // Reducido de -43 a -25
                    pageWidth - margin - 5,
                    yPosition + baseLineHeight - 15, // Reducido de -43 a -25
                    1f, 0f, 0f, 0f
            );

            //ROW 4
            float[] columnWidthsRow4 = {150, 155, 42, 223};
            String[] headerTextsRow4 = {"MEDICAMENTO", "PRESENTACIÓN", "CANT", "INDICACIONES"};
            boolean[] coloredCellsRow4 = new boolean[4];
            coloredCellsRow4[0] = false;
            coloredCellsRow4[1] = false;
            coloredCellsRow4[2] = false;
            coloredCellsRow4[3] = false;
            boolean[] coloredCellsRow4_ = new boolean[4];
            coloredCellsRow4_[0] = true;
            coloredCellsRow4_[1] = true;
            coloredCellsRow4_[2] = true;
            coloredCellsRow4_[3] = true;

            float rowHeight3 = this.reportServiceImpl.calculateRowHeightPersonSinBordes(
                    headerTextsRow4,
                    columnWidthsRow4,
                    fontRegular,
                    medicalPrescription.getColor().getFontSize()
            );
            this.reportServiceImpl.drawCustomRow(
                    contentStream,
                    currentX,
                    yPosition,
                    columnWidthsRow4,
                    rowHeight3,
                    headerTextsRow4,
                    fontRegular, fontBold,
                    medicalPrescription.getColor().getFontSize(),
                    false,
                    coloredCellsRow4,
                    coloredCellsRow4_,
                    medicalPrescription.getColor().getRed(), //red - más claro
                    medicalPrescription.getColor().getGreen(), //green - más claro  
                    medicalPrescription.getColor().getBlue(), //blue
                    false
            );
            yPosition -= rowHeight3;

            this.reportServiceImpl.drawLine(
                    contentStream,
                    margin,
                    yPosition + baseLineHeight - 16, // Reducido de -43 a -25
                    pageWidth - margin - 5,
                    yPosition + baseLineHeight - 16, // Reducido de -43 a -25
                    1f, 0f, 0f, 0f
            );

            int rowIndex = 0;
            for (MedicationDto m : medicalPrescription.getMedications()) {
                float[] columnWidthsRow5 = {150, 155, 42, 223};
                String instructionsText = m.getInstructions() != null ? this.reportServiceImpl.convertirAMayusculas(m.getInstructions()) : "";
                String medicationText = m.getMedication() != null ? this.reportServiceImpl.convertirAMayusculas(m.getMedication()) : "";
                String presentationText = m.getPresentation() != null ? this.reportServiceImpl.convertirAMayusculas(m.getPresentation()) : "";
                String quantityText = m.getQuantity();

                // Determinar si es fila impar para aplicar color de fondo
                boolean isOddRow = rowIndex % 2 != 0; // Cambiado a impar
                boolean[] coloredCellsRow5;
                boolean[] coloredCellsRow5_;

                if (isOddRow) { // Cambiado a impar
                    // Si es fila impar, pintar todas las celdas
                    coloredCellsRow5 = new boolean[]{true, true, true, true};
                    coloredCellsRow5_ = new boolean[]{false, false, false, false};
                } else {
                    // Si es fila par, no pintar ninguna
                    coloredCellsRow5 = new boolean[]{false, false, false, false};
                    coloredCellsRow5_ = new boolean[]{false, false, false, false};
                }

                float requiredHeight = this.reportServiceImpl.calculateRowHeightPersonSinBordesMedications(
                        new String[]{medicationText, presentationText, quantityText, instructionsText},
                        columnWidthsRow5,
                        fontRegular,
                        medicalPrescription.getColor().getFontSize()
                );

                // Cálculo DIRECTO - Primera página
                float availableHeight = yPosition - margin;

                if (requiredHeight > availableHeight) {
                    // Para páginas siguientes
                    float otherPagesHeight = pageHeight - margin;

                    // Dividir solo las instrucciones (columna más ancha que puede tener texto largo)
                    List<String> instructionPages = this.reportServiceImpl.splitTextIntoPagesOptimized(
                            instructionsText,
                            fontRegular,
                            medicalPrescription.getColor().getFontSize(),
                            columnWidthsRow5[3] - 8, // Ancho de la columna Instructions menos padding
                            availableHeight, // Primera página
                            otherPagesHeight // Páginas siguientes
                    );

                    if (!instructionPages.isEmpty()) {
                        // Primera parte en página actual
                        String[] headerTextsRow5 = {
                            medicationText,
                            presentationText,
                            quantityText,
                            instructionPages.get(0)
                        };

                        float rowHeight5 = this.reportServiceImpl.calculateRowHeightPerson(
                                headerTextsRow5,
                                columnWidthsRow5,
                                fontRegular,
                                medicalPrescription.getColor().getFontSize()
                        );

                        this.reportServiceImpl.drawCustomRow(
                                contentStream,
                                currentX,
                                yPosition,
                                columnWidthsRow5,
                                rowHeight5,
                                headerTextsRow5,
                                fontRegular, fontBold,
                                medicalPrescription.getColor().getFontSize(),
                                false,
                                coloredCellsRow5, // Aplicar color de fondo si es impar
                                coloredCellsRow5_,
                                isOddRow ? 0.9f : medicalPrescription.getColor().getRed(), // Gris claro para filas impares
                                isOddRow ? 0.9f : medicalPrescription.getColor().getGreen(),
                                isOddRow ? 0.9f : medicalPrescription.getColor().getBlue(),
                                false
                        );
                        yPosition -= rowHeight5;

                        // Partes restantes de las instrucciones
                        for (int i = 1; i < instructionPages.size(); i++) {
                            float continuationHeight = this.reportServiceImpl.calculateRowHeightPersonSinBordesMedications(
                                    new String[]{"", "", "", instructionPages.get(i)},
                                    columnWidthsRow5,
                                    fontRegular,
                                    medicalPrescription.getColor().getFontSize()
                            );

                            // Verificar si cabe en página actual
                            if (yPosition - continuationHeight < margin) {
                                contentStream.close();
                                PDPage newPage = new PDPage(PDRectangle.A4);
                                document.addPage(newPage);
                                contentStream = new PDPageContentStream(document, newPage);
                                yPosition = pageHeight - margin;
                            }

                            // LIMITAR LA ALTURA DE CONTINUACIÓN AL ESPACIO DISPONIBLE
                            float newPageAvailableHeight = yPosition - margin;
                            continuationHeight = Math.min(continuationHeight, newPageAvailableHeight);

                            // Para las continuaciones, dejamos las primeras tres columnas vacías
                            String[] continuationTexts = {"", "", "", instructionPages.get(i)};

                            this.reportServiceImpl.drawCustomRow(
                                    contentStream,
                                    currentX,
                                    yPosition,
                                    columnWidthsRow5,
                                    continuationHeight,
                                    continuationTexts,
                                    fontRegular, fontBold,
                                    medicalPrescription.getColor().getFontSize(),
                                    false,
                                    coloredCellsRow5, // Aplicar color de fondo si es impar
                                    coloredCellsRow5_,
                                    isOddRow ? 0.9f : medicalPrescription.getColor().getRed(), // Gris claro para filas impares
                                    isOddRow ? 0.9f : medicalPrescription.getColor().getGreen(),
                                    isOddRow ? 0.9f : medicalPrescription.getColor().getBlue(),
                                    false
                            );
                            yPosition -= continuationHeight;
                        }
                    }
                } else {
                    // Cabe en una sola página
                    String[] headerTextsRow5 = {
                        medicationText,
                        presentationText,
                        quantityText,
                        instructionsText
                    };

                    float rowHeight5 = this.reportServiceImpl.calculateRowHeightPersonSinBordesMedications(
                            headerTextsRow5,
                            columnWidthsRow5,
                            fontRegular,
                            medicalPrescription.getColor().getFontSize()
                    );

                    this.reportServiceImpl.drawCustomRow(
                            contentStream,
                            currentX,
                            yPosition,
                            columnWidthsRow5,
                            rowHeight5,
                            headerTextsRow5,
                            fontRegular, fontBold,
                            medicalPrescription.getColor().getFontSize(),
                            false,
                            coloredCellsRow5, // Aplicar color de fondo si es impar
                            coloredCellsRow5_,
                            isOddRow ? 0.9f : medicalPrescription.getColor().getRed(), // Gris claro para filas impares
                            isOddRow ? 0.9f : medicalPrescription.getColor().getGreen(),
                            isOddRow ? 0.9f : medicalPrescription.getColor().getBlue(),
                            false
                    );
                    yPosition -= rowHeight5;
                }

                // Incrementar el contador de filas
                rowIndex++;
            }

            //Salto
            yPosition -= baseLineSalto;

            this.reportServiceImpl.drawLine(
                    contentStream,
                    margin,
                    yPosition + baseLineHeight - 15, // Reducido de -43 a -25
                    pageWidth - margin - 5,
                    yPosition + baseLineHeight - 15, // Reducido de -43 a -25
                    1f, 0f, 0f, 0f
            );

            //ROW 6
            float[] columnWidthsRow6 = {80, 490};
            String[] headerTextsRow6 = {"CÓDIGO", "DIAGNÓSTICO"};
            boolean[] coloredCellsRow6 = new boolean[2];
            coloredCellsRow6[0] = false;
            coloredCellsRow6[1] = false;
            boolean[] coloredCellsRow6_ = new boolean[2];
            coloredCellsRow6_[0] = true;
            coloredCellsRow6_[1] = true;

            float rowHeight5 = this.reportServiceImpl.calculateRowHeightPersonSinBordesMedications(
                    headerTextsRow6,
                    columnWidthsRow6,
                    fontRegular,
                    medicalPrescription.getColor().getFontSize()
            );
            if (yPosition - rowHeight5 - 10 < margin) {
                contentStream.close();
                PDPage newPage = new PDPage(PDRectangle.A4);
                document.addPage(newPage);
                contentStream = new PDPageContentStream(document, newPage);
                yPosition = pageHeight - margin;
            }
            this.reportServiceImpl.drawCustomRow(
                    contentStream,
                    currentX,
                    yPosition,
                    columnWidthsRow6,
                    rowHeight5,
                    headerTextsRow6,
                    fontRegular, fontBold,
                    medicalPrescription.getColor().getFontSize(),
                    false,
                    coloredCellsRow6,
                    coloredCellsRow6_,
                    medicalPrescription.getColor().getRed(), //red - más claro
                    medicalPrescription.getColor().getGreen(), //green - más claro  
                    medicalPrescription.getColor().getBlue(), //blue
                    false
            );
            yPosition -= rowHeight5;

            this.reportServiceImpl.drawLine(
                    contentStream,
                    margin,
                    yPosition + baseLineHeight - 15, // Reducido de -43 a -25
                    pageWidth - margin - 5,
                    yPosition + baseLineHeight - 15, // Reducido de -43 a -25
                    1f, 0f, 0f, 0f
            );

            float minFooterHeight = 100f; // Reducido de 150f a 100f
            int diagnosisRowIndex = 0;
            for (DiagnosesDto d : medicalPrescription.getDiagnoses()) {
                float[] columnWidthsRow7 = {80, 490};
                String diagnosisText = d.getDiagnoses() != null ? d.getDiagnoses() : "";
                String codeText = d.getCode();
//                String diagnosisText = d.getDiagnoses() != null ? this.reportServiceImpl.convertirAMayusculas(d.getDiagnoses()) : "";
//                String codeText = this.reportServiceImpl.convertirAMayusculas(d.getCode());

                // Determinar si es fila impar para aplicar color de fondo
                boolean isOddRow = diagnosisRowIndex % 2 != 0;
                boolean[] coloredCellsRow7;
                boolean[] coloredCellsRow7_;

                if (isOddRow) {
                    // Si es fila impar, pintar todas las celdas
                    coloredCellsRow7 = new boolean[]{true, true};
                    coloredCellsRow7_ = new boolean[]{false, false};
                } else {
                    // Si es fila par, no pintar ninguna
                    coloredCellsRow7 = new boolean[]{false, false};
                    coloredCellsRow7_ = new boolean[]{false, false};
                }

                // Calcular la altura necesaria para el texto completo del diagnóstico
                float requiredHeight = this.reportServiceImpl.calculateRowHeightPersonSinBordesMedications(
                        new String[]{codeText, diagnosisText},
                        columnWidthsRow7,
                        fontRegular,
                        medicalPrescription.getColor().getFontSizeMayusculasMinusculas()
                );

                // CÁLCULO MÁS AGRESIVO - Usar casi todo el espacio disponible
                float availableHeight = yPosition - margin - minFooterHeight;

                if (requiredHeight > availableHeight) {
                    // Para páginas siguientes - espacio completo menos footer mínimo
                    float otherPagesHeight = pageHeight - margin - minFooterHeight;

                    List<String> diagnosisPages = this.reportServiceImpl.splitTextIntoPagesOptimized(
                            diagnosisText,
                            fontRegular,
                            medicalPrescription.getColor().getFontSizeMayusculasMinusculas(),
                            columnWidthsRow7[1] - 8,
                            availableHeight,
                            otherPagesHeight
                    );

                    if (!diagnosisPages.isEmpty()) {
                        // Primera parte en página actual
                        String[] headerTextsRow7 = {codeText, diagnosisPages.get(0)};

                        float rowHeight7 = this.reportServiceImpl.calculateRowHeightPersonSinBordesMedications(
                                headerTextsRow7,
                                columnWidthsRow7,
                                fontRegular,
                                medicalPrescription.getColor().getFontSizeMayusculasMinusculas()
                        );

                        this.reportServiceImpl.drawCustomRow(
                                contentStream,
                                currentX,
                                yPosition,
                                columnWidthsRow7,
                                rowHeight7,
                                headerTextsRow7,
                                fontRegular, fontBold,
                                medicalPrescription.getColor().getFontSizeMayusculasMinusculas(),
                                false,
                                coloredCellsRow7, // Aplicar color de fondo si es impar
                                coloredCellsRow7_,
                                isOddRow ? 0.9f : 230f / 255f, // Gris claro para filas impares, azul claro para pares
                                isOddRow ? 0.9f : 230f / 255f,
                                isOddRow ? 0.9f : 255f / 255f,
                                false
                        );
                        yPosition -= rowHeight7;

                        // Partes restantes - manejo más inteligente de páginas
                        for (int i = 1; i < diagnosisPages.size(); i++) {
                            float continuationHeight = this.reportServiceImpl.calculateRowHeightPersonSinBordesMedications(
                                    new String[]{"", diagnosisPages.get(i)},
                                    columnWidthsRow7,
                                    fontRegular,
                                    medicalPrescription.getColor().getFontSizeMayusculasMinusculas()
                            );

                            // VERIFICACIÓN MÁS AGRESIVA DEL ESPACIO
                            // Solo crear nueva página si realmente no cabe
                            if (yPosition - continuationHeight < margin + minFooterHeight) {
                                contentStream.close();
                                PDPage newPage = new PDPage(PDRectangle.A4);
                                document.addPage(newPage);
                                contentStream = new PDPageContentStream(document, newPage);
                                yPosition = pageHeight - margin;
                            }

                            // LIMITAR LA ALTURA DE CONTINUACIÓN AL ESPACIO DISPONIBLE
                            float newPageAvailableHeight = yPosition - margin - minFooterHeight;
                            continuationHeight = Math.min(continuationHeight, newPageAvailableHeight);

                            String[] continuationTexts = {"", diagnosisPages.get(i)};

                            this.reportServiceImpl.drawCustomRow(
                                    contentStream,
                                    currentX,
                                    yPosition,
                                    columnWidthsRow7,
                                    continuationHeight,
                                    continuationTexts,
                                    fontRegular, fontBold,
                                    medicalPrescription.getColor().getFontSizeMayusculasMinusculas(),
                                    false,
                                    coloredCellsRow7, // Aplicar color de fondo si es impar
                                    coloredCellsRow7_,
                                    isOddRow ? 0.9f : 230f / 255f, // Gris claro para filas impares, azul claro para pares
                                    isOddRow ? 0.9f : 230f / 255f,
                                    isOddRow ? 0.9f : 255f / 255f,
                                    false
                            );
                            yPosition -= continuationHeight;
                        }
                    }
                } else {
                    // Cabe en una sola página
                    String[] headerTextsRow7 = {codeText, diagnosisText};

                    float rowHeight7 = this.reportServiceImpl.calculateRowHeightPersonSinBordesMedications(
                            headerTextsRow7,
                            columnWidthsRow7,
                            fontRegular,
                            medicalPrescription.getColor().getFontSizeMayusculasMinusculas()
                    );

                    this.reportServiceImpl.drawCustomRow(
                            contentStream,
                            currentX,
                            yPosition,
                            columnWidthsRow7,
                            rowHeight7,
                            headerTextsRow7,
                            fontRegular, fontBold,
                            medicalPrescription.getColor().getFontSizeMayusculasMinusculas(),
                            false,
                            coloredCellsRow7, // Aplicar color de fondo si es impar
                            coloredCellsRow7_,
                            isOddRow ? 0.9f : 230f / 255f, // Gris claro para filas impares, azul claro para pares
                            isOddRow ? 0.9f : 230f / 255f,
                            isOddRow ? 0.9f : 255f / 255f,
                            false
                    );
                    yPosition -= rowHeight7;
                }

                // Incrementar el contador de filas de diagnósticos
                diagnosisRowIndex++;
            }

            // ===Salto===
            yPosition -= baseLineHeight;

            // === SECCIÓN DE CONTACTOS ===
            ContactData contactData = ContactData.builder()
                    .phones(medicalPrescription.getPhones())
                    .email(medicalPrescription.getEmail())
                    .website(medicalPrescription.getWebsite())
                    .fontRegular(fontRegular)
                    .fontBold(fontBold)
                    .fontSize(medicalPrescription.getColor().getFontSize())
                    .color(medicalPrescription.getColor())
                    .build();

            // Verificar si hay espacio para los contactos
            float contactHeight = contactDrawer.calculateRequiredHeight(contactData);
            if (yPosition - contactHeight < margin) {
                contentStream.close();
                PDPage newPage = new PDPage(PDRectangle.A4);
                document.addPage(newPage);
                contentStream = new PDPageContentStream(document, newPage);
                yPosition = pageHeight - margin;
            }

            try {
                yPosition = contactDrawer.drawContactInfo(
                        contentStream,
                        contactData,
                        yPosition,
                        margin,
                        currentX,
                        pageWidth
                );
            } catch (Exception ex) {
                Logger.getLogger(ReportRecetaMedicaServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            // ===Salto===
            yPosition -= baseLineHeight;

            // ===Firma===
            this.reportServiceImpl.drawMergedCellWithoutBorders(
                    contentStream,
                    fontBold,
                    "FIRMA: ______________________",
                    currentX,
                    yPosition,
                    totalBaseWidth,
                    baseLineHeight,
                    TextAlignment.RIGHT,
                    false,
                    230f / 255f, //red - más claro
                    230f / 255f, //green - más claro  
                    255f / 255f, //blue
                    medicalPrescription.getColor().getFontSize(),
                    0.3f
            );
            yPosition -= baseLineHeight;

            contentStream.close();
            document.save(baos);

        } catch (IOException ex) {
            Logger.getLogger(ReportRecetaMedicaServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                document.close();
            } catch (IOException ex) {
                Logger.getLogger(ReportRecetaMedicaServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return baos;
    }
}
