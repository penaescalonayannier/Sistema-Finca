package com.kynsoft.report.infrastructure.services.reporte;

import com.kynsoft.report.domain.dto.seccionesGenericas.TableDrawerResult;
import com.kynsoft.report.domain.dto.seccionesGenericas.TableRowData;
import com.kynsoft.report.domain.dto.seccionesGenericas.TableSectionData;
import com.kynsoft.report.domain.services.IReportServicePdfBox;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

@Component
@AllArgsConstructor
public class TableSectionDrawer {

    private final IReportServicePdfBox reportService;

    // Cambiamos el retorno para incluir tanto la posición Y como el contentStream actualizado
    public TableDrawerResult drawTableSection(
            PDDocument document,
            PDPageContentStream contentStream,
            TableSectionData tableData
    ) throws IOException {
        
        float yPosition = tableData.getStartY();
        float margin = tableData.getMargin();
        float pageHeight = tableData.getPageHeight();
        float minFooterHeight = tableData.getMinFooterHeight();
        float currentX = tableData.getCurrentX();
        float pageWidth = tableData.getPageWidth();
        float lineOffset = tableData.getLineOffset();

        // Dibujar línea encima del encabezado si está configurado
        if (tableData.isDrawLineAboveHeader()) {
            reportService.drawLine(
                    contentStream,
                    margin,
                    yPosition + tableData.getBaseLineHeight() + lineOffset,
                    pageWidth - margin - 5,
                    yPosition + tableData.getBaseLineHeight() + lineOffset,
                    1f, 0f, 0f, 0f
            );
        }

        // Dibujar encabezado de la tabla
        float headerHeight = reportService.calculateRowHeightPersonSinBordesMedications(
                tableData.getHeaders(),
                tableData.getColumnWidths(),
                tableData.getFontRegular(),
                tableData.getFontSize()
        );

        // Verificar espacio para el encabezado
        if (yPosition - headerHeight < margin + minFooterHeight) {
            contentStream.close(); // Cerrar el stream actual
            PDPage newPage = new PDPage(PDRectangle.A4);
            document.addPage(newPage);
            contentStream = new PDPageContentStream(document, newPage);
            yPosition = pageHeight - margin;
        }

        // Dibujar encabezado
        reportService.drawCustomRow(
                contentStream,
                currentX,
                yPosition,
                tableData.getColumnWidths(),
                headerHeight,
                tableData.getHeaders(),
                tableData.getFontRegular(),
                tableData.getFontBold(),
                tableData.getFontSize(),
                false,
                tableData.getHeaderColoredCells(),
                tableData.getHeaderBoldCells(),
                tableData.getColor().getRed(),
                tableData.getColor().getGreen(),
                tableData.getColor().getBlue(),
                false
        );
        yPosition -= headerHeight;

        // Dibujar línea después del encabezado si está configurado
        if (tableData.isDrawLineBelowHeader()) {
            reportService.drawLine(
                    contentStream,
                    margin,
                    yPosition + tableData.getBaseLineHeight() + lineOffset,
                    pageWidth - margin - 5,
                    yPosition + tableData.getBaseLineHeight() + lineOffset,
                    1f, 0f, 0f, 0f
            );
        }

        // Dibujar filas de datos
        int rowIndex = 0;
        for (TableRowData rowData : tableData.getRows()) {
            TableDrawerResult result = drawTableRow(document, contentStream, tableData, rowData, rowIndex, yPosition);
            yPosition = result.getYPosition();
            contentStream = result.getContentStream(); // Actualizar el contentStream
            rowIndex++;
        }

        return new TableDrawerResult(yPosition, contentStream);
    }

    private TableDrawerResult drawTableRow(
            PDDocument document,
            PDPageContentStream contentStream,
            TableSectionData tableData,
            TableRowData rowData,
            int rowIndex,
            float startY
    ) throws IOException {
        
        float yPosition = startY;
        float margin = tableData.getMargin();
        float pageHeight = tableData.getPageHeight();
        float minFooterHeight = tableData.getMinFooterHeight();
        float currentX = tableData.getCurrentX();

        // Determinar si es fila impar para aplicar color de fondo
        boolean isOddRow = rowIndex % 2 != 0;
        boolean[] coloredCells;
        boolean[] boldCells;

        if (isOddRow) {
            coloredCells = new boolean[rowData.getCellData().length];
            boldCells = new boolean[rowData.getCellData().length];
            for (int i = 0; i < coloredCells.length; i++) {
                coloredCells[i] = true;
                boldCells[i] = false;
            }
        } else {
            coloredCells = new boolean[rowData.getCellData().length];
            boldCells = new boolean[rowData.getCellData().length];
            for (int i = 0; i < coloredCells.length; i++) {
                coloredCells[i] = false;
                boldCells[i] = false;
            }
        }

        // Calcular altura requerida
        float requiredHeight = reportService.calculateRowHeightPersonSinBordesMedications(
                rowData.getCellData(),
                tableData.getColumnWidths(),
                tableData.getFontRegular(),
                tableData.getFontSize()
        );

        // Calcular espacio disponible
        float availableHeight = yPosition - margin - minFooterHeight;

        if (requiredHeight > availableHeight && rowData.isSplittable()) {
            // Manejar división de texto largo
            return handleSplitRow(document, contentStream, tableData, rowData, isOddRow, coloredCells, yPosition);
        } else {
            // Dibujar fila normal
            reportService.drawCustomRow(
                    contentStream,
                    currentX,
                    yPosition,
                    tableData.getColumnWidths(),
                    requiredHeight,
                    rowData.getCellData(),
                    tableData.getFontRegular(),
                    tableData.getFontBold(),
                    tableData.getFontSize(),
                    false,
                    coloredCells,
                    boldCells,
                    isOddRow ? 0.9f : tableData.getColor().getRed(),
                    isOddRow ? 0.9f : tableData.getColor().getGreen(),
                    isOddRow ? 0.9f : tableData.getColor().getBlue(),
                    false
            );
            return new TableDrawerResult(yPosition - requiredHeight, contentStream);
        }
    }

    private TableDrawerResult handleSplitRow(
            PDDocument document,
            PDPageContentStream contentStream,
            TableSectionData tableData,
            TableRowData rowData,
            boolean isOddRow,
            boolean[] coloredCells,
            float startY
    ) throws IOException {
        
        float yPosition = startY;
        float margin = tableData.getMargin();
        float pageHeight = tableData.getPageHeight();
        float minFooterHeight = tableData.getMinFooterHeight();
        float currentX = tableData.getCurrentX();

        // Obtener índice de la columna que se puede dividir (generalmente la última)
        int splitColumnIndex = rowData.getCellData().length - 1;
        String longText = rowData.getCellData()[splitColumnIndex];

        // Dividir el texto
        List<String> textPages = reportService.splitTextIntoPagesOptimized(
                longText,
                tableData.getFontRegular(),
                tableData.getFontSize(),
                tableData.getColumnWidths()[splitColumnIndex] - 8,
                yPosition - margin - minFooterHeight,
                pageHeight - margin - minFooterHeight
        );

        if (!textPages.isEmpty()) {
            // Primera parte
            String[] firstRowData = rowData.getCellData().clone();
            firstRowData[splitColumnIndex] = textPages.get(0);

            float firstRowHeight = reportService.calculateRowHeightPersonSinBordesMedications(
                    firstRowData,
                    tableData.getColumnWidths(),
                    tableData.getFontRegular(),
                    tableData.getFontSize()
            );

            reportService.drawCustomRow(
                    contentStream,
                    currentX,
                    yPosition,
                    tableData.getColumnWidths(),
                    firstRowHeight,
                    firstRowData,
                    tableData.getFontRegular(),
                    tableData.getFontBold(),
                    tableData.getFontSize(),
                    false,
                    coloredCells,
                    new boolean[firstRowData.length],
                    isOddRow ? 0.9f : tableData.getColor().getRed(),
                    isOddRow ? 0.9f : tableData.getColor().getGreen(),
                    isOddRow ? 0.9f : tableData.getColor().getBlue(),
                    false
            );
            yPosition -= firstRowHeight;

            // Partes restantes
            for (int i = 1; i < textPages.size(); i++) {
                // CORRECCIÓN: Crear array con la longitud correcta
                String[] continuationData = new String[rowData.getCellData().length];
                for (int j = 0; j < continuationData.length; j++) {
                    continuationData[j] = (j == splitColumnIndex) ? textPages.get(i) : "";
                }

                float continuationHeight = reportService.calculateRowHeightPersonSinBordesMedications(
                        continuationData,
                        tableData.getColumnWidths(),
                        tableData.getFontRegular(),
                        tableData.getFontSize()
                );

                if (yPosition - continuationHeight < margin + minFooterHeight) {
                    contentStream.close(); // Cerrar el stream actual
                    PDPage newPage = new PDPage(PDRectangle.A4);
                    document.addPage(newPage);
                    contentStream = new PDPageContentStream(document, newPage);
                    yPosition = pageHeight - margin;
                }

                float newPageAvailableHeight = yPosition - margin - minFooterHeight;
                continuationHeight = Math.min(continuationHeight, newPageAvailableHeight);

                reportService.drawCustomRow(
                        contentStream,
                        currentX,
                        yPosition,
                        tableData.getColumnWidths(),
                        continuationHeight,
                        continuationData,
                        tableData.getFontRegular(),
                        tableData.getFontBold(),
                        tableData.getFontSize(),
                        false,
                        coloredCells,
                        new boolean[continuationData.length],
                        isOddRow ? 0.9f : tableData.getColor().getRed(),
                        isOddRow ? 0.9f : tableData.getColor().getGreen(),
                        isOddRow ? 0.9f : tableData.getColor().getBlue(),
                        false
                );
                yPosition -= continuationHeight;
            }
        }

        return new TableDrawerResult(yPosition, contentStream);
    }
}