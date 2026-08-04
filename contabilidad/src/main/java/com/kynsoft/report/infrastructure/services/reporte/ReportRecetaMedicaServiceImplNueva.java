package com.kynsoft.report.infrastructure.services.reporte;

import com.kynsoft.report.domain.dto.seccionesGenericas.TableRowData;
import com.kynsoft.report.domain.dto.seccionesGenericas.TableDrawerResult;
import com.kynsoft.report.domain.dto.seccionesGenericas.TableSectionData;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReportRecetaMedicaServiceImplNueva {

    private final IReportServicePdfBox reportServiceImpl;
    private final HeaderDrawerOther headerDrawer;
    private final ContactDrawer contactDrawer;
    private final TableSectionDrawer tableSectionDrawer;

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
                Logger.getLogger(ReportRecetaMedicaServiceImplNueva.class.getName()).log(Level.SEVERE, null, ex);
            }

            // En constructionReport, reemplazar la sección de medicamentos:
            List<TableRowData> medicationRows = medicalPrescription.getMedications().stream()
                    .map(m -> TableRowData.splittable(
                    m.getMedication() != null ? this.reportServiceImpl.capitalizarOracionesAvanzado(m.getMedication()) : "",
                    m.getPresentation() != null ? this.reportServiceImpl.capitalizarOracionesAvanzado(m.getPresentation()) : "",
                    m.getQuantity(),
                    m.getInstructions() != null ? this.reportServiceImpl.capitalizarOracionesAvanzado(m.getInstructions()) : ""
            ))
                    .collect(Collectors.toList());

            TableSectionData medicationsTableData = TableSectionData.builder()
                    .columnWidths(new float[]{150, 155, 42, 223})
                    .headers(new String[]{"Medicamento", "Presentación", "Cant", "Indicaciones"})
                    .rows(medicationRows)
                    .headerColoredCells(new boolean[]{false, false, false, false})
                    .headerBoldCells(new boolean[]{true, true, true, true})
                    .fontRegular(fontRegular)
                    .fontBold(fontBold)
                    .fontSize(medicalPrescription.getColor().getFontSize())
                    .color(medicalPrescription.getColor())
                    .startY(yPosition)
                    .margin(margin)
                    .pageHeight(pageHeight)
                    .pageWidth(pageWidth)
                    .currentX(currentX)
                    .minFooterHeight(80f)
                    .baseLineHeight(baseLineHeight)
                    .drawLineAboveHeader(true) // Línea encima del encabezado
                    .drawLineBelowHeader(true) // Línea debajo del encabezado
                    .lineOffset(-20f) // Ajuste de posición
                    .build();

            TableDrawerResult result = tableSectionDrawer.drawTableSection(document, contentStream, medicationsTableData);
            yPosition = result.getYPosition();
            contentStream = result.getContentStream(); // Actualiza el contentStream

            // Para una sola columna, debes crear los TableRowData con arrays de un solo elemento:
            List<TableRowData> medicationRows1 = new ArrayList<>();
            for (MedicationDto m : medicalPrescription.getMedications()) {
                String medicationText = m.getMedication() != null
                        ? this.reportServiceImpl.capitalizarOracionesAvanzado(m.getInstructions()) : "";

                // CORRECCIÓN: Array con un solo elemento para una columna
                TableRowData rowData = TableRowData.splittable(medicationText);
                medicationRows1.add(rowData);
            }

            TableSectionData medicationsTableData1 = TableSectionData.builder()
                    .columnWidths(new float[]{570})
                    .headers(new String[]{"Observaciones"})
                    .rows(medicationRows1)
                    .headerColoredCells(new boolean[]{false})
                    .headerBoldCells(new boolean[]{true})
                    .fontRegular(fontRegular)
                    .fontBold(fontBold)
                    .fontSize(medicalPrescription.getColor().getFontSize())
                    .color(medicalPrescription.getColor())
                    .startY(yPosition)
                    .margin(margin)
                    .pageHeight(pageHeight)
                    .pageWidth(pageWidth)
                    .currentX(currentX)
                    .minFooterHeight(80f)
                    .baseLineHeight(baseLineHeight)
                    .drawLineAboveHeader(true)
                    .drawLineBelowHeader(true)
                    .lineOffset(-20f)
                    .build();

            TableDrawerResult result1 = tableSectionDrawer.drawTableSection(document, contentStream, medicationsTableData1);
            yPosition = result1.getYPosition();
            contentStream = result1.getContentStream(); // Actualiza el contentStream
            // Y para diagnósticos:
            List<TableRowData> diagnosisRows = medicalPrescription.getDiagnoses().stream()
                    .map(d -> TableRowData.splittable(
                    d.getCode(),
                    d.getDiagnoses() != null ? this.reportServiceImpl.capitalizarOracionesAvanzado(d.getDiagnoses()) : ""
            ))
                    .collect(Collectors.toList());

            TableSectionData diagnosesTableData = TableSectionData.builder()
                    .columnWidths(new float[]{50, 520})
                    .headers(new String[]{"Código", "Diagnóstico"})
                    .rows(diagnosisRows)
                    .headerColoredCells(new boolean[]{false, false})
                    .headerBoldCells(new boolean[]{true, true})
                    .fontRegular(fontRegular)
                    .fontBold(fontBold)
                    .fontSize(medicalPrescription.getColor().getFontSize() - 2f)
                    .color(medicalPrescription.getColor())
                    .startY(yPosition)
                    .margin(margin)
                    .pageHeight(pageHeight)
                    .pageWidth(pageWidth)
                    .currentX(currentX)
                    .minFooterHeight(80f)
                    .baseLineHeight(baseLineHeight)
                    .drawLineAboveHeader(true) // Sin línea encima
                    .drawLineBelowHeader(true) // Con línea debajo
                    .lineOffset(-15f) // Ajuste de posición
                    .build();
            TableDrawerResult result2 = tableSectionDrawer.drawTableSection(document, contentStream, diagnosesTableData);
            yPosition = result2.getYPosition();
            contentStream = result2.getContentStream(); // Actualiza el contentStream

            // ===Salto===
            this.reportServiceImpl.drawMergedCellWithoutBorders(
                    contentStream,
                    fontBold,
                    "",
                    currentX,
                    yPosition,
                    totalBaseWidth,
                    baseLineHeight,
                    TextAlignment.CENTER,
                    false,
                    medicalPrescription.getColor().getRed(), //red - más claro
                    medicalPrescription.getColor().getGreen(), //green - más claro  
                    medicalPrescription.getColor().getBlue(), //blue
                    medicalPrescription.getColor().getFontSize(),
                    0.3f
            );
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
                Logger.getLogger(ReportRecetaMedicaServiceImplNueva.class.getName()).log(Level.SEVERE, null, ex);
            }

            // ===Salto===
            this.reportServiceImpl.drawMergedCellWithoutBorders(
                    contentStream,
                    fontBold,
                    "",
                    currentX,
                    yPosition,
                    totalBaseWidth,
                    baseLineHeight,
                    TextAlignment.CENTER,
                    false,
                    medicalPrescription.getColor().getRed(), //red - más claro
                    medicalPrescription.getColor().getGreen(), //green - más claro  
                    medicalPrescription.getColor().getBlue(), //blue
                    medicalPrescription.getColor().getFontSize(),
                    0.3f
            );
            yPosition -= baseLineHeight;

            // ===Firma===
            this.reportServiceImpl.drawMergedCellWithoutBorders(
                    contentStream,
                    fontBold,
                    "Firma: ______________________",
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
            Logger.getLogger(ReportRecetaMedicaServiceImplNueva.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                document.close();
            } catch (IOException ex) {
                Logger.getLogger(ReportRecetaMedicaServiceImplNueva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return baos;
    }
}
