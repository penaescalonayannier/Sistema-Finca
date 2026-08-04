package com.kynsoft.report.infrastructure.services.reporte;

import com.kynsoft.report.domain.services.IReportServicePdfBox;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ContactDrawer {

    private final IReportServicePdfBox reportService;

    public float drawContactInfo(
            PDPageContentStream contentStream,
            ContactData contactData,
            float startY,
            float margin,
            float currentX,
            float pageWidth
    ) throws Exception {

        float yPosition = startY;
        float baseLineHeight = 14f;

        // Verificar si hay espacio suficiente antes de dibujar
        float requiredHeight = calculateRequiredHeight(contactData);
        if (yPosition - requiredHeight < margin) {
            // En una implementación completa, aquí manejarías la creación de nueva página
            // Por ahora retornamos la posición actual para indicar que no hay espacio
            return yPosition;
        }

        // Dibujar teléfono
        if (contactData.getPhones() != null && !contactData.getPhones().trim().isEmpty()) {
            float[] columnWidths = {80, 280};
            String[] texts = {"Teléfono", contactData.getPhones()};
            boolean[] coloredCells = {true, false};

            float rowHeight = reportService.calculateRowHeightPerson(
                    texts,
                    columnWidths,
                    contactData.getFontRegular(),
                    10f
            );

            reportService.drawCustomRow(
                    contentStream,
                    currentX,
                    yPosition,
                    columnWidths,
                    rowHeight,
                    texts,
                    contactData.getFontRegular(),
                    contactData.getFontBold(),
                    10f,
                    false,
                    coloredCells,
                    coloredCells,
                    contactData.getColor().getRed(),
                    contactData.getColor().getGreen(),
                    contactData.getColor().getBlue()
            );
            yPosition -= rowHeight;
        }

        // Dibujar correo
        if (contactData.getEmail() != null && !contactData.getEmail().trim().isEmpty()) {
            float[] columnWidths = {80, 280};
            String[] texts = {"Correo", contactData.getEmail()};
            boolean[] coloredCells = {true, false};

            float rowHeight = reportService.calculateRowHeightPerson(
                    texts,
                    columnWidths,
                    contactData.getFontRegular(),
                    10f
            );

            reportService.drawCustomRow(
                    contentStream,
                    currentX,
                    yPosition,
                    columnWidths,
                    rowHeight,
                    texts,
                    contactData.getFontRegular(),
                    contactData.getFontBold(),
                    10f,
                    false,
                    coloredCells,
                    coloredCells,
                    contactData.getColor().getRed(),
                    contactData.getColor().getGreen(),
                    contactData.getColor().getBlue()
            );
            yPosition -= rowHeight;
        }

        // Dibujar sitio web
        if (contactData.getWebsite() != null && !contactData.getWebsite().trim().isEmpty()) {
            float[] columnWidths = {80, 280};
            String[] texts = {"Sitio web", contactData.getWebsite()};
            boolean[] coloredCells = {true, false};

            float rowHeight = reportService.calculateRowHeightPerson(
                    texts,
                    columnWidths,
                    contactData.getFontRegular(),
                    10f
            );

            reportService.drawCustomRow(
                    contentStream,
                    currentX,
                    yPosition,
                    columnWidths,
                    rowHeight,
                    texts,
                    contactData.getFontRegular(),
                    contactData.getFontBold(),
                    10f,
                    false,
                    coloredCells,
                    coloredCells,
                    contactData.getColor().getRed(),
                    contactData.getColor().getGreen(),
                    contactData.getColor().getBlue()
            );
            yPosition -= rowHeight;
        }

        return yPosition;
    }

    public float calculateRequiredHeight(ContactData contactData) {
        float totalHeight = 0;

        if (contactData.getPhones() != null && !contactData.getPhones().trim().isEmpty()) {
            try {
                float[] columnWidths = {80, 280};
                String[] texts = {"Teléfono", contactData.getPhones()};
                totalHeight += reportService.calculateRowHeightPerson(
                        texts, columnWidths, contactData.getFontRegular(), 10f
                );
            } catch (IOException ex) {
                Logger.getLogger(ContactDrawer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (contactData.getEmail() != null && !contactData.getEmail().trim().isEmpty()) {
            try {
                float[] columnWidths = {80, 280};
                String[] texts = {"Correo", contactData.getEmail()};
                totalHeight += reportService.calculateRowHeightPerson(
                        texts, columnWidths, contactData.getFontRegular(), 10f
                );
            } catch (IOException ex) {
                Logger.getLogger(ContactDrawer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (contactData.getWebsite() != null && !contactData.getWebsite().trim().isEmpty()) {
            try {
                float[] columnWidths = {80, 280};
                String[] texts = {"Sitio web", contactData.getWebsite()};
                totalHeight += reportService.calculateRowHeightPerson(
                        texts, columnWidths, contactData.getFontRegular(), 10f
                );
            } catch (IOException ex) {
                Logger.getLogger(ContactDrawer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return totalHeight;
    }
}
