package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.status.TextAlignment;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.List;

public interface IReportServicePdfBox {

    String convertirAMayusculas(String texto);

    String capitalizarOracionesAvanzado(String texto);

    String capitalizarNombrePropio(String nombre);

    void drawCustomRow(PDPageContentStream contentStream, float startX, float startY,
            float[] columnWidths, float rowHeight,
            String[] texts, PDType1Font fontRegular, PDType1Font fontBold, float fontSize,
            boolean centered, boolean[] coloredCells, boolean[] boldCells,
            float red, float green, float blue, boolean drawBorders) throws IOException;
    void drawCustomRow(PDPageContentStream contentStream, float startX, float startY,
            float[] columnWidths, float rowHeight,
            String[] texts, PDType1Font fontRegular, PDType1Font fontBold, float fontSize,
            boolean centered, boolean[] coloredCells, boolean[] boldCells,
            float red, float green, float blue, boolean drawBorders, boolean div) throws IOException;

    void drawTwoTextsInSameLine(PDPageContentStream contentStream,
            PDType1Font leftFont, // Nueva: fuente específica para texto izquierdo
            PDType1Font rightFont, // Nueva: fuente específica para texto derecho
            String leftText,
            String rightText,
            float x,
            float y,
            float width,
            float height,
            boolean withBackground,
            float red,
            float green,
            float blue,
            float fontSize,
            float lineSpacing) throws IOException;

    float calculateRowHeightPersonSinBordes(String[] texts, float[] columnWidths, PDType1Font font, float fontSize) throws IOException;

    float calculateRowHeightPersonSinBordesMedications(String[] texts, float[] columnWidths, PDType1Font font, float fontSize) throws IOException;

    void drawMergedCell(PDPageContentStream contentStream, PDType1Font font, String text, float x, float y, float width, float height, boolean centered) throws IOException;

    float getTotalWidth(float[] columnWidths, int start, int count);

    void drawCustomRow(PDPageContentStream contentStream, float startX, float startY,
            float[] columnWidths, float rowHeight,
            String[] texts, PDType1Font font, boolean centered, boolean[] coloredCells,
            float red, float green, float blue) throws IOException;

    void drawCustomRow(PDPageContentStream contentStream, float startX, float startY,
            float[] columnWidths, float rowHeight,
            String[] texts, PDType1Font fontRegular, PDType1Font fontBold, float fontSize,
            boolean centered, boolean[] coloredCells, boolean[] boldCells,
            float red, float green, float blue) throws IOException;

    void drawMergedCellWithoutBorders(PDPageContentStream contentStream, PDType1Font font, String text,
            float x, float y, float width, float height, TextAlignment alignment,
            boolean withBackground, float red, float green, float blue, float fontSize,
            float lineSpacing) throws IOException;

    void drawLine(PDPageContentStream contentStream,
            float startX, float startY,
            float endX, float endY,
            float lineWidth, float red, float green, float blue) throws IOException;

    float calculateRowHeight(String[] texts, float[] columnWidths, PDType1Font font, float fontSize) throws IOException;

    float calculateRowHeightPerson(String[] texts, float[] columnWidths, PDType1Font font, float fontSize) throws IOException;

    float calculateRowHeightPersonDiagnosis2Cell(String[] texts, float[] columnWidths, PDType1Font font, float fontSize) throws IOException;

    PDPageContentStream createNewPage(PDDocument document, PDPageContentStream currentContentStream) throws IOException;

    void drawBase64Image(PDDocument document, PDPageContentStream contentStream,
            String base64Image, float x, float y, float width, float height) throws IOException;

    void drawVerticalMergedCellsFlexible(PDPageContentStream contentStream, float startX, float startY,
            float[][] columnWidthsPerRow, float rowHeight,
            String[][] rowTexts, int mergeColumns,
            PDType1Font font, boolean centered) throws IOException;

    void drawCustomRowPages(PDPageContentStream contentStream, float startX, float startY,
            float[] columnWidths, float rowHeight,
            String[] texts, PDType1Font font, float fontSize, boolean centered, boolean[] coloredCells,
            float red, float green, float blue) throws IOException;

    List<String> splitTextIntoPages(String text, PDType1Font font, float fontSize,
            float maxWidth, float maxHeight) throws IOException;

    List<String> splitTextIntoPagesOptimized(String text, PDType1Font font, float fontSize,
            float maxWidth, float firstPageHeight,
            float otherPagesHeight) throws IOException;
}
