package com.kynsoft.report.infrastructure.services.reporte;

import com.kynsoft.report.domain.services.IReportServicePdfBox;
import com.kynsoft.report.domain.dto.status.TextAlignment;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FooterDrawer {

    private final IReportServicePdfBox reportService;

    public float drawSignature(
            PDPageContentStream contentStream,
            PDType1Font fontBold,
            float startY,
            float currentX,
            float totalBaseWidth,
            float fontSize
    ) throws Exception {

        float yPosition = startY;
        float lineHeight = 12f;

        // FIRMA inmediatamente después de los contactos
        this.reportService.drawMergedCellWithoutBorders(
                contentStream,
                fontBold,
                "Firma: ______________________",
                currentX,
                yPosition,
                totalBaseWidth,
                lineHeight,
                TextAlignment.LEFT,
                false,
                0f, // Color negro
                0f,
                0f,
                fontSize,
                0.1f
        );
        yPosition -= lineHeight;

        return yPosition;
    }
}