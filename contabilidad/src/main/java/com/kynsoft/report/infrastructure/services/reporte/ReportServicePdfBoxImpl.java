package com.kynsoft.report.infrastructure.services.reporte;

import com.kynsoft.report.domain.dto.status.TextAlignment;
import com.kynsoft.report.domain.services.IReportServicePdfBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReportServicePdfBoxImpl implements IReportServicePdfBox {

    @Override
    public String convertirAMayusculas(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return texto;
        }
        return texto.toUpperCase();
    }

    /**
     * Capitaliza oraciones con manejo avanzado de casos especiales. - Maneja
     * abreviaciones comunes (Dr., Sr., etc.) - Preserva saltos de línea -
     * Maneja múltiples signos de puntuación
     *
     * @param texto
     * @return
     */
    @Override
    public String capitalizarOracionesAvanzado(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }

        // PRIMERO: Convertir todo a minúsculas
        texto = texto.toLowerCase();

        // Abreviaciones comunes que no terminan oración
        Set<String> abreviaciones = new HashSet<>(Arrays.asList(
                "dr", "sr", "sra", "srta", "lic", "ing", "etc", "tel", "av"
        ));

        StringBuilder resultado = new StringBuilder(texto.length());
        boolean capitalizarSiguiente = true;

        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);

            // Capitalizar si es letra y está marcado
            if (capitalizarSiguiente && Character.isLetter(c)) {
                resultado.append(Character.toUpperCase(c));
                capitalizarSiguiente = false;
            } else {
                resultado.append(c);
            }

            // Detectar finales de oración
            if (c == '.' || c == '!' || c == '?') {
                // Verificar si es una abreviación
                if (c == '.' && !esAbreviacion(texto, i, abreviaciones)) {
                    capitalizarSiguiente = true;
                } else if (c == '!' || c == '?') {
                    capitalizarSiguiente = true;
                }
            }

            // Capitalizar después de salto de línea
            if (c == '\n') {
                capitalizarSiguiente = true;
            }
        }

        return resultado.toString();
    }

    /**
     * Capitaliza nombres propios respetando artículos, conjunciones y
     * preposiciones. - Capitaliza la primera letra de cada palabra
     * significativa - Respeta minúsculas en artículos, conjunciones y
     * preposiciones - Mantiene abreviaciones como Dr., Sr., etc.
     *
     * @param nombre El nombre a capitalizar
     * @return El nombre con capitalización apropiada para nombres propios
     */
    @Override
    public String capitalizarNombrePropio(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return nombre;
        }

        // Convertir todo a minúsculas primero
        nombre = nombre.toLowerCase();

        // Lista de palabras que NO se capitalizan (excepto si son la primera palabra)
        Set<String> noCapitalizar = new HashSet<>(Arrays.asList(
                "y", "e", "o", "u", "de", "del", "la", "las", "lo", "los",
                "el", "un", "una", "unos", "unas", "al", "con", "por", "para",
                "sin", "sobre", "entre", "hacia", "a", "en", "se", "su", "es"
        ));

        // Abreviaciones comunes
        Set<String> abreviaciones = new HashSet<>(Arrays.asList(
                "dr", "sr", "sra", "srta", "lic", "ing", "etc", "tel", "av"
        ));

        StringBuilder resultado = new StringBuilder(nombre.length());
        String[] palabras = nombre.split("\\s+");

        for (int i = 0; i < palabras.length; i++) {
            String palabra = palabras[i];

            if (palabra.isEmpty()) {
                continue;
            }

            // Si es la primera palabra o no está en la lista de no capitalizar, capitalizar
            if (i == 0 || !noCapitalizar.contains(palabra)) {
                // Verificar si es una abreviación
                if (abreviaciones.contains(palabra.toLowerCase()) && i < palabras.length - 1) {
                    // Es una abreviación, capitalizar y agregar punto
                    resultado.append(Character.toUpperCase(palabra.charAt(0)))
                            .append(palabra.substring(1))
                            .append(".");
                } else {
                    // Capitalizar normalmente
                    resultado.append(Character.toUpperCase(palabra.charAt(0)))
                            .append(palabra.substring(1));
                }
            } else {
                // Palabra que no se capitaliza (artículos, conjunciones, etc.)
                resultado.append(palabra);
            }

            // Agregar espacio si no es la última palabra
            if (i < palabras.length - 1) {
                resultado.append(" ");
            }
        }

        return resultado.toString();
    }

    private static boolean esAbreviacion(String texto, int posicionPunto, Set<String> abreviaciones) {
        // Extraer palabra antes del punto
        int inicio = posicionPunto - 1;
        while (inicio >= 0 && Character.isLetter(texto.charAt(inicio))) {
            inicio--;
        }
        inicio++;

        if (inicio < posicionPunto) {
            String palabra = texto.substring(inicio, posicionPunto).toLowerCase();
            return abreviaciones.contains(palabra);
        }

        return false;
    }

    /**
     * MÉTODO PARA CELDAS COMBINADAS
     *
     * @param contentStream
     * @param font
     * @param text
     * @param x
     * @param y
     * @param width
     * @param height
     * @param centered
     * @throws IOException
     */
    @Override
    public void drawMergedCell(PDPageContentStream contentStream, PDType1Font font, String text,
            float x, float y, float width, float height, boolean centered) throws IOException {

        // Convertir RGB (0-255) a valores decimales (0.0-1.0) para PDFBox
        float red = 204f / 255f;   // 204 → 0.8
        float green = 204f / 255f; // 204 → 0.8
        float blue = 255f / 255f;  // 255 → 1.0

        // Primero: dibujar el fondo de la celda con el color #ccccff (azul lavanda)
        contentStream.setNonStrokingColor(red, green, blue);
        contentStream.addRect(x, y - height, width, height);
        contentStream.fill();

        // Segundo: dibujar el borde de la celda
        contentStream.setStrokingColor(0f, 0f, 0f); // Borde negro (0,0,0)
        contentStream.setLineWidth(0.4f);
        contentStream.addRect(x, y - height, width, height);
        contentStream.stroke();

        // Tercero: dibujar el texto
        if (text != null && !text.trim().isEmpty()) {
            contentStream.beginText();
            contentStream.setFont(font, 9);
            contentStream.setNonStrokingColor(0f, 0f, 0f); // Texto negro

            // Alineación a la izquierda - siempre usa x + 3 (margen izquierdo)
            float textX = x + 3;
            float textY = y - height + (height - 11) / 2 + 3;

            contentStream.newLineAtOffset(textX, textY);
            contentStream.showText(text);
            contentStream.endText();
        }
    }

    /**
     * MÉTODO FLEXIBLE PARA COMBINAR CELDAS VERTICALMENTE CON DIFERENTES
     * ESTRUCTURAS DE FILA
     *
     * @param contentStream
     * @param startX
     * @param startY
     * @param columnWidthsPerRow
     * @param rowHeight
     * @param rowTexts
     * @param mergeColumns
     * @param font
     * @param centered
     * @throws IOException
     */
    @Override
    public void drawVerticalMergedCellsFlexible(PDPageContentStream contentStream, float startX, float startY,
            float[][] columnWidthsPerRow, float rowHeight,
            String[][] rowTexts, int mergeColumns,
            PDType1Font font, boolean centered) throws IOException {

        float currentX = startX;
        float currentY = startY;
        int totalRows = rowTexts.length;

        // Color verde claro #ccffcc (RGB: 204, 255, 204)
        float red = 204f / 255f;
        float green = 255f / 255f;
        float blue = 204f / 255f;

        // PRIMERO: Dibujar el fondo verde para TODAS las celdas de las columnas combinadas
        contentStream.setNonStrokingColor(red, green, blue);

        for (int col = 0; col < mergeColumns; col++) {
            float colWidth = columnWidthsPerRow[0][col]; // Ancho de esta columna
            float colX = currentX;

            // Dibujar fondo verde para cada fila en esta columna
            for (int row = 0; row < totalRows; row++) {
                float rowY = currentY - (row * rowHeight) - rowHeight;
                contentStream.addRect(colX, rowY, colWidth, rowHeight);
            }

            currentX += colWidth;
        }
        contentStream.fill();

        // Resetear currentX para el siguiente paso
        currentX = startX;

        // SEGUNDO: Dibujar los bordes y texto para las celdas combinadas verticalmente
        for (int col = 0; col < mergeColumns; col++) {
            // Calcular la altura total de la celda combinada
            float totalHeight = rowHeight * totalRows;

            // Dibujar el borde de la celda combinada
            contentStream.setStrokingColor(0f, 0f, 0f);
            contentStream.setLineWidth(0.2f);
            contentStream.addRect(currentX, currentY - totalHeight, columnWidthsPerRow[0][col], totalHeight);
            contentStream.stroke();

            // Centrar el texto verticalmente en la celda combinada
            String text = rowTexts[0][col]; // Tomar el texto de la primera fila
            if (text != null && !text.trim().isEmpty()) {
                contentStream.beginText();
                contentStream.setFont(font, 6f);
                contentStream.setNonStrokingColor(0f, 0f, 0f); // Texto negro

                float textWidth = getTextWidth(text, font, 6f);
                float textX = centered ? currentX + (columnWidthsPerRow[0][col] - textWidth) / 2 : currentX + 4;
                float textY = currentY - (totalHeight / 2) - 2;

                contentStream.newLineAtOffset(textX, textY);
                contentStream.showText(text);
                contentStream.endText();
            }

            currentX += columnWidthsPerRow[0][col];
        }

        // TERCERO: Dibujar las celdas individuales para cada fila
        for (int row = 0; row < totalRows; row++) {
            float rowY = currentY - (row * rowHeight);
            float tempX = currentX;

            // Dibujar las celdas no combinadas de esta fila
            for (int col = mergeColumns; col < columnWidthsPerRow[row].length; col++) {
                String text = (rowTexts[row] != null && col < rowTexts[row].length) ? rowTexts[row][col] : null;

                if (text != null) {
                    drawCellWithTextWrap(contentStream, font, text, tempX, rowY,
                            columnWidthsPerRow[row][col], rowHeight, centered);
                } else {
                    drawEmptyCell(contentStream, tempX, rowY, columnWidthsPerRow[row][col], rowHeight);
                }

                tempX += columnWidthsPerRow[row][col];
            }
        }
    }

    private void drawEmptyCell(PDPageContentStream contentStream, float x, float y, float width, float height) throws IOException {
        contentStream.setLineWidth(0.4f);
        contentStream.addRect(x, y - height, width, height);
        contentStream.stroke();
    }

    /**
     * MÉTODO PARA DIBUJAR CELDA CON TEXTO AJUSTADO (MEJORADO PARA ESPACIADO
     * VERTICAL)
     *
     * @param contentStream
     * @param font
     * @param text
     * @param x
     * @param y
     * @param width
     * @param height
     * @param centered
     * @throws IOException
     */
    private void drawCellWithTextWrap(PDPageContentStream contentStream, PDType1Font font, String text,
            float x, float y, float width, float height, boolean centered) throws IOException {
        // Dibujar borde de la celda
        contentStream.setLineWidth(0.4f);
        contentStream.addRect(x, y - height, width, height);
        contentStream.stroke();

        if (text != null && !text.trim().isEmpty()) {
            float fontSize = 6f;
            float maxWidth = width - 8;
            List<String> lines = wrapText(text, font, maxWidth, fontSize);
            float lineSpacing = 7f;
            float totalTextHeight = lines.size() * lineSpacing;

            // CÁLCULO MEJORADO PARA CENTRADO VERTICAL
            float startY = y - (height / 2) + (totalTextHeight / 2) - (lineSpacing / 2);

            for (int i = 0; i < lines.size(); i++) {
                contentStream.beginText();
                contentStream.setFont(font, fontSize);
                String line = lines.get(i);
                float lineWidth = getTextWidth(line, font, fontSize);
                float textX = centered ? x + (width - lineWidth) / 2 : x + 4;
                float textY = startY - (i * lineSpacing);
                contentStream.newLineAtOffset(textX, textY);
                contentStream.showText(line);
                contentStream.endText();
            }
        }
    }

    @Override
    public void drawMergedCellWithoutBorders(PDPageContentStream contentStream, PDType1Font font, String text,
            float x, float y, float width, float height, TextAlignment alignment,
            boolean withBackground, float red, float green, float blue, float fontSize,
            float lineSpacing) throws IOException {

        // Si se solicita fondo, dibujar el fondo de la celda con el color especificado
        if (withBackground) {
            contentStream.setNonStrokingColor(red, green, blue);
            contentStream.addRect(x, y - height, width, height);
            contentStream.fill();
        }

        // Dibujar el texto
        if (text != null && !text.trim().isEmpty()) {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.setNonStrokingColor(0f, 0f, 0f);

            float textX;
            float textWidth = getTextWidth(text, font, fontSize);
            float margin = 3f;

            switch (alignment) {
                case LEFT:
                    textX = x + margin;
                    break;
                case CENTER:
                    textX = x + (width - textWidth) / 2;
                    break;
                case RIGHT:
                    textX = x + width - textWidth - margin;
                    break;
                default:
                    textX = x + margin;
            }

            // Ajustar posición Y según el tamaño de fuente y el espacio entre líneas
            float textY = y - height + (height - fontSize) / 2 + (fontSize * lineSpacing);

            contentStream.newLineAtOffset(textX, textY);
            contentStream.showText(text);
            contentStream.endText();
        }
    }

    @Override
    public void drawTwoTextsInSameLine(PDPageContentStream contentStream,
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
            float lineSpacing) throws IOException {

        // Si se solicita fondo, dibujar el fondo de la celda con el color especificado
        if (withBackground) {
            contentStream.setNonStrokingColor(red, green, blue);
            contentStream.addRect(x, y - height, width, height);
            contentStream.fill();
        }

        // Ajustar posición Y
        float textY = y - height + (height - fontSize) / 2 + (fontSize * lineSpacing);
        float margin = 3f;

        // Dibujar texto izquierdo con su fuente específica
        if (leftText != null && !leftText.trim().isEmpty()) {
            contentStream.beginText();
            contentStream.setFont(leftFont, fontSize); // Usar fuente izquierda
            contentStream.setNonStrokingColor(0f, 0f, 0f);

            float leftTextX = x + margin;
            contentStream.newLineAtOffset(leftTextX, textY);
            contentStream.showText(leftText);
            contentStream.endText();
        }

        // Dibujar texto derecho con su fuente específica
        if (rightText != null && !rightText.trim().isEmpty()) {
            contentStream.beginText();
            contentStream.setFont(rightFont, fontSize); // Usar fuente derecha
            contentStream.setNonStrokingColor(0f, 0f, 0f);

            float rightTextWidth = getTextWidth(rightText, rightFont, fontSize);
            float rightTextX = x + width - rightTextWidth - margin;

            contentStream.newLineAtOffset(rightTextX, textY);
            contentStream.showText(rightText);
            contentStream.endText();
        }
    }
//
//// Método auxiliar para calcular el ancho del texto
//    private float getTextWidth(String text, PDType1Font font, float fontSize) throws IOException {
//        return font.getStringWidth(text) / 1000 * fontSize;
//    }
//
//    @Override
//    public void drawMergedCellWithoutBorders(PDPageContentStream contentStream, PDType1Font font, String text,
//            float x, float y, float width, float height, TextAlignment alignment,
//            boolean withBackground, float red, float green, float blue, float fontSize,
//            float lineSpacing) throws IOException {
//
//        // Si se solicita fondo, dibujar el fondo de la celda con el color especificado
//        if (withBackground) {
//            contentStream.setNonStrokingColor(red, green, blue);
//            contentStream.addRect(x, y - height, width, height);
//            contentStream.fill();
//        }
//
//        // Dibujar el texto
//        if (text != null && !text.trim().isEmpty()) {
//            contentStream.beginText();
//            contentStream.setFont(font, fontSize);
//            contentStream.setNonStrokingColor(0f, 0f, 0f);
//
//            float textX;
//            float textWidth = getTextWidth(text, font, fontSize);
//            float margin = 3f;
//
//            switch (alignment) {
//                case LEFT:
//                    textX = x + margin;
//                    break;
//                case CENTER:
//                    textX = x + (width - textWidth) / 2;
//                    break;
//                case RIGHT:
//                    textX = x + width - textWidth - margin;
//                    break;
//                default:
//                    textX = x + margin;
//            }
//
//            // Ajustar posición Y según el tamaño de fuente y el espacio entre líneas
//            float textY = y - height + (height - fontSize) / 2 + (fontSize * lineSpacing);
//
//            contentStream.newLineAtOffset(textX, textY);
//            contentStream.showText(text);
//            contentStream.endText();
//        }
//    }

    @Override
    public void drawLine(PDPageContentStream contentStream,
            float startX, float startY,
            float endX, float endY,
            float lineWidth, float red, float green, float blue) throws IOException {

        contentStream.setStrokingColor(red, green, blue);
        contentStream.setLineWidth(lineWidth);
        contentStream.moveTo(startX, startY);
        contentStream.lineTo(endX, endY);
        contentStream.stroke();
    }

    // Métodos auxiliares
    @Override
    public float getTotalWidth(float[] columnWidths, int start, int count) {
        float total = 0;
        for (int i = start; i < start + count && i < columnWidths.length; i++) {
            total += columnWidths[i];
        }
        return total;
    }

    @Override
    public void drawCustomRow(PDPageContentStream contentStream, float startX, float startY,
            float[] columnWidths, float rowHeight,
            String[] texts, PDType1Font font, boolean centered, boolean[] coloredCells,
            float red, float green, float blue) throws IOException {

        float currentX = startX;
        float currentY = startY;

        contentStream.setStrokingColor(0f, 0f, 0f);
        contentStream.setLineWidth(0.5f);

        // Determinar si el color de fondo es claro u oscuro (para decidir color del texto)
        boolean isLightBackground = (red > 0.6f && green > 0.6f && blue > 0.6f);

        // Usar el array coloredCells recibido por parámetro
        for (int i = 0; i < columnWidths.length; i++) {
            String text = i < texts.length ? texts[i] : "";

            // Aplicar fondo con color personalizado si la celda está marcada en coloredCells
            if (coloredCells != null && i < coloredCells.length && coloredCells[i]) {
                contentStream.setNonStrokingColor(red, green, blue);
                contentStream.addRect(currentX, currentY - rowHeight, columnWidths[i], rowHeight);
                contentStream.fill();
            }

            // Dibujar borde de la celda
            contentStream.addRect(currentX, currentY - rowHeight, columnWidths[i], rowHeight);
            contentStream.stroke();

            // Dibujar texto si existe
            if (text != null && !text.trim().isEmpty()) {
                //text = text.replace("\n", " ").replace("\r", " ").trim();
                contentStream.setFont(font, 6f);

                // Para celdas coloreadas: texto blanco si fondo oscuro, negro si fondo claro
                if (coloredCells != null && i < coloredCells.length && coloredCells[i]) {
                    if (isLightBackground) {
                        contentStream.setNonStrokingColor(0f, 0f, 0f); // Texto negro para fondos claros
                    } else {
                        contentStream.setNonStrokingColor(1f, 1f, 1f); // Texto blanco para fondos oscuros
                    }
                } else {
                    contentStream.setNonStrokingColor(0f, 0f, 0f); // Texto negro para celdas sin color
                }

                // DIVIDIR TEXTO PARA TODAS LAS CELDAS, NO SOLO LAS PARES
                List<String> lines = splitTextIntoLines(text, font, 6f, columnWidths[i] - 4);

                float lineHeight = 7f;
                float totalTextHeight = lines.size() * lineHeight;

                // Posición vertical centrada
                float startTextY = currentY - ((rowHeight - totalTextHeight) / 2) - (lineHeight * 0.7f);

                for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                    String line = lines.get(lineIndex);
                    float textWidth = getTextWidth(line, font, 6f);

                    float textX;

                    // USAR LA VARIABLE CENTERED PARA DETERMINAR LA ALINEACIÓN
                    if (centered) {
                        // Centrar el texto horizontalmente
                        textX = currentX + (columnWidths[i] - textWidth) / 2;
                    } else {
                        // Alinear a la izquierda con pequeño margen
                        textX = currentX + 2;
                    }

                    float textY = startTextY - (lineIndex * lineHeight);

                    contentStream.beginText();
                    contentStream.newLineAtOffset(textX, textY);
                    contentStream.showText(line);
                    contentStream.endText();
                }
            }

            currentX += columnWidths[i];
        }
    }

    @Override
    public void drawCustomRow(PDPageContentStream contentStream, float startX, float startY,
            float[] columnWidths, float rowHeight,
            String[] texts, PDType1Font fontRegular, PDType1Font fontBold, float fontSize,
            boolean centered, boolean[] coloredCells, boolean[] boldCells,
            float red, float green, float blue) throws IOException {

        float currentX = startX;
        float currentY = startY;

        contentStream.setStrokingColor(0f, 0f, 0f);
        contentStream.setLineWidth(0.5f);

        // Determinar si el color de fondo es claro u oscuro (para decidir color del texto)
        boolean isLightBackground = (red > 0.6f && green > 0.6f && blue > 0.6f);

        // Usar el array coloredCells recibido por parámetro
        for (int i = 0; i < columnWidths.length; i++) {
            String text = i < texts.length ? texts[i] : "";

            // Determinar qué fuente usar para esta celda
            PDType1Font currentFont = fontRegular;
            if (boldCells != null && i < boldCells.length && boldCells[i]) {
                currentFont = fontBold;
            }

            // Aplicar fondo con color personalizado si la celda está marcada en coloredCells
            if (coloredCells != null && i < coloredCells.length && coloredCells[i]) {
                contentStream.setNonStrokingColor(red, green, blue);
                contentStream.addRect(currentX, currentY - rowHeight, columnWidths[i], rowHeight);
                contentStream.fill();
            }

            // Dibujar borde de la celda
            contentStream.addRect(currentX, currentY - rowHeight, columnWidths[i], rowHeight);
            contentStream.stroke();

            // Dibujar texto si existe
            if (text != null && !text.trim().isEmpty()) {
                contentStream.setFont(currentFont, fontSize);

                // Para celdas coloreadas: texto blanco si fondo oscuro, negro si fondo claro
                if (coloredCells != null && i < coloredCells.length && coloredCells[i]) {
                    if (isLightBackground) {
                        contentStream.setNonStrokingColor(0f, 0f, 0f); // Texto negro para fondos claros
                    } else {
                        contentStream.setNonStrokingColor(1f, 1f, 1f); // Texto blanco para fondos oscuros
                    }
                } else {
                    contentStream.setNonStrokingColor(0f, 0f, 0f); // Texto negro para celdas sin color
                }

                // DIVIDIR TEXTO CON LA FUENTE CORRECTA
                List<String> lines = splitTextIntoLines(text, currentFont, fontSize, columnWidths[i] - 4);

                float lineHeight = fontSize + 1f;

                // CALCULAR CUÁNTAS LÍNEAS CABEN EN LA ALTURA DE LA FILA
                int maxLinesInRow = (int) ((rowHeight - 4) / lineHeight); // -4 para padding
                maxLinesInRow = Math.max(1, maxLinesInRow); // Mínimo 1 línea

                // LIMITAR LAS LÍNEAS A LAS QUE CABEN EN LA FILA
                int linesToRender = Math.min(lines.size(), maxLinesInRow);
                float totalTextHeight = linesToRender * lineHeight;

                // Posición vertical centrada
                float startTextY = currentY - ((rowHeight - totalTextHeight) / 2) - (lineHeight * 0.7f);

                for (int lineIndex = 0; lineIndex < linesToRender; lineIndex++) {
                    String line = lines.get(lineIndex);
                    float textWidth = getTextWidth(line, currentFont, fontSize);

                    float textX;

                    // USAR LA VARIABLE CENTERED PARA DETERMINAR LA ALINEACIÓN
                    if (centered) {
                        // Centrar el texto horizontalmente
                        textX = currentX + (columnWidths[i] - textWidth) / 2;
                    } else {
                        // Alinear a la izquierda con pequeño margen
                        textX = currentX + 2;
                    }

                    float textY = startTextY - (lineIndex * lineHeight);

                    // VERIFICACIÓN ADICIONAL: No renderizar si está fuera de la celda
                    if (textY >= currentY - rowHeight) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(textX, textY);
                        contentStream.showText(line);
                        contentStream.endText();
                    }
                }
            }

            currentX += columnWidths[i];
        }
    }

    /***
     * Este metodo es el generalment usado.
     * @param contentStream
     * @param startX
     * @param startY
     * @param columnWidths
     * @param rowHeight
     * @param texts
     * @param fontRegular
     * @param fontBold
     * @param fontSize
     * @param centered
     * @param coloredCells
     * @param boldCells
     * @param red
     * @param green
     * @param blue
     * @param drawBorders
     * @throws IOException 
     */
    @Override
    public void drawCustomRow(PDPageContentStream contentStream, float startX, float startY,
            float[] columnWidths, float rowHeight,
            String[] texts, PDType1Font fontRegular, PDType1Font fontBold, float fontSize,
            boolean centered, boolean[] coloredCells, boolean[] boldCells,
            float red, float green, float blue, boolean drawBorders) throws IOException {

        float currentX = startX;
        float currentY = startY;
        float lineSpacing = 1.1f;

        // Configurar color y ancho de línea solo si se dibujan bordes
        if (drawBorders) {
            contentStream.setStrokingColor(0f, 0f, 0f);
            contentStream.setLineWidth(0.5f);
        }

        // Determinar si el color de fondo es claro u oscuro (para decidir color del texto)
        boolean isLightBackground = (red > 0.6f && green > 0.6f && blue > 0.6f);

        // Padding ajustado
        float paddingH = 2f;

        for (int i = 0; i < columnWidths.length; i++) {
            String text = i < texts.length ? texts[i] : "";

            // Determinar qué fuente usar para esta celda
            PDType1Font currentFont = fontRegular;
            if (boldCells != null && i < boldCells.length && boldCells[i]) {
                currentFont = fontBold;
            }

            // Aplicar fondo con color personalizado si la celda está marcada en coloredCells
            if (coloredCells != null && i < coloredCells.length && coloredCells[i]) {
                contentStream.setNonStrokingColor(red, green, blue);
                contentStream.addRect(currentX, currentY - rowHeight, columnWidths[i], rowHeight);
                contentStream.fill();
            }

            // Dibujar borde de la celda solo si drawBorders es true
            if (drawBorders) {
                contentStream.addRect(currentX, currentY - rowHeight, columnWidths[i], rowHeight);
                contentStream.stroke();
            }

            // Dibujar texto si existe
            if (text != null && !text.trim().isEmpty()) {
                contentStream.setFont(currentFont, fontSize);

                // Para celdas coloreadas: texto blanco si fondo oscuro, negro si fondo claro
                if (coloredCells != null && i < coloredCells.length && coloredCells[i]) {
                    if (isLightBackground) {
                        contentStream.setNonStrokingColor(0f, 0f, 0f);
                    } else {
                        contentStream.setNonStrokingColor(1f, 1f, 1f);
                    }
                } else {
                    contentStream.setNonStrokingColor(0f, 0f, 0f);
                }

                List<String> lines = splitTextIntoLines(text, currentFont, fontSize, columnWidths[i]);

                // Altura de línea con interlineado
                float lineHeight = fontSize * lineSpacing;

                // Cálculo de líneas que caben
                int maxLinesInRow = (int) (rowHeight / lineHeight);
                maxLinesInRow = Math.max(1, maxLinesInRow);

                int linesToRender = Math.min(lines.size(), maxLinesInRow);
                float totalTextHeight = linesToRender * lineHeight;

                // AJUSTE FINAL: Agregar un pequeño offset para separar más
                // El valor 0.2f es el ajuste clave - prueba con diferentes valores
                float verticalAdjustment = fontSize * 0.2f;

                // Fórmula con ajuste adicional
                float startTextY = currentY - ((rowHeight - totalTextHeight) / 2) - lineHeight + verticalAdjustment;

                for (int lineIndex = 0; lineIndex < linesToRender; lineIndex++) {
                    String line = lines.get(lineIndex);
                    float textWidth = getTextWidth(line, currentFont, fontSize);

                    float textX;
                    if (centered) {
                        textX = currentX + (columnWidths[i] - textWidth) / 2;
                    } else {
                        textX = currentX + paddingH;
                    }

                    float textY = startTextY - (lineIndex * lineHeight);

                    // Dibujar el texto
                    contentStream.beginText();
                    contentStream.newLineAtOffset(textX, textY);
                    contentStream.showText(line);
                    contentStream.endText();
                }
            }

            currentX += columnWidths[i];
        }
    }

    @Override
    public void drawCustomRow(PDPageContentStream contentStream, float startX, float startY,
            float[] columnWidths, float rowHeight,
            String[] texts, PDType1Font fontRegular, PDType1Font fontBold, float fontSize,
            boolean centered, boolean[] coloredCells, boolean[] boldCells,
            float red, float green, float blue, boolean drawBorders, boolean div) throws IOException {

        float currentX = startX;
        float currentY = startY;

        // Configurar color y ancho de línea solo si se dibujan bordes
        if (drawBorders) {
            contentStream.setStrokingColor(0f, 0f, 0f);
            contentStream.setLineWidth(0.5f);
        }

        // Determinar si el color de fondo es claro u oscuro (para decidir color del texto)
        boolean isLightBackground = (red > 0.6f && green > 0.6f && blue > 0.6f);

        // REDUCIDO: Padding mucho más compacto
        float paddingH = 1f; // Reducido de 2-4 a 1
        float paddingV = 1f; // Reducido vertical padding

        for (int i = 0; i < columnWidths.length; i++) {
            String text = i < texts.length ? texts[i] : "";

            // Determinar qué fuente usar para esta celda
            PDType1Font currentFont = fontRegular;
            if (boldCells != null && i < boldCells.length && boldCells[i]) {
                currentFont = fontBold;
            }

            // Aplicar fondo con color personalizado si la celda está marcada en coloredCells
            if (coloredCells != null && i < coloredCells.length && coloredCells[i]) {
                contentStream.setNonStrokingColor(red, green, blue);
                contentStream.addRect(currentX, currentY - rowHeight, columnWidths[i], rowHeight);
                contentStream.fill();
            }

            // Dibujar borde de la celda solo si drawBorders es true
            if (drawBorders) {
                contentStream.addRect(currentX, currentY - rowHeight, columnWidths[i], rowHeight);
                contentStream.stroke();
            }

            // Dibujar texto si existe
            if (text != null && !text.trim().isEmpty()) {
                contentStream.setFont(currentFont, fontSize);

                // Para celdas coloreadas: texto blanco si fondo oscuro, negro si fondo claro
                if (coloredCells != null && i < coloredCells.length && coloredCells[i]) {
                    if (isLightBackground) {
                        contentStream.setNonStrokingColor(0f, 0f, 0f);
                    } else {
                        contentStream.setNonStrokingColor(1f, 1f, 1f);
                    }
                } else {
                    contentStream.setNonStrokingColor(0f, 0f, 0f);
                }

                // MODIFICADO: Usar ancho completo sin restar padding para split
                List<String> lines = splitTextIntoLines(text, currentFont, fontSize, columnWidths[i]);

                float lineHeight = fontSize + 0.5f; // REDUCIDO: Menor espacio entre líneas

                // MODIFICADO: Cálculo más compacto de líneas que caben
                int maxLinesInRow = (int) (rowHeight / lineHeight);
                maxLinesInRow = Math.max(1, maxLinesInRow);

                // LIMITAR LAS LÍNEAS A LAS QUE CABEN EN LA FILA
                int linesToRender = Math.min(lines.size(), maxLinesInRow);
                float totalTextHeight = linesToRender * lineHeight;

                // MODIFICADO: Posición vertical más compacta - eliminado el * 0.7f
                float startTextY = currentY - ((rowHeight - totalTextHeight) / 2) - lineHeight;

                for (int lineIndex = 0; lineIndex < linesToRender; lineIndex++) {
                    String line = lines.get(lineIndex);
                    float textWidth = getTextWidth(line, currentFont, fontSize);

                    float textX;

                    if (centered) {
                        textX = currentX + (columnWidths[i] - textWidth) / 2;
                    } else {
                        textX = currentX + paddingH; // REDUCIDO: Solo padding mínimo
                    }

                    float textY = startTextY - (lineIndex * lineHeight);

                    // MODIFICADO: Condición más permisiva para renderizar
                    if (textY >= currentY - rowHeight - 2) { // Permitir un poco más abajo
                        contentStream.beginText();
                        contentStream.newLineAtOffset(textX, textY);
                        contentStream.showText(line);
                        contentStream.endText();
                    }
                }
            }

            currentX += columnWidths[i];
        }

        // NUEVO: Dibujar línea divisoria cuando hay exactamente 2 textos
        if (texts.length == 2 && columnWidths.length >= 2) {
            // Guardar el estado actual del graphics state
            contentStream.saveGraphicsState();

            // Configurar la línea divisoria
            contentStream.setStrokingColor(0.5f, 0.5f, 0.5f); // Color gris medio
            contentStream.setLineWidth(0.8f); // Ligeramente más gruesa que los bordes normales

            // Calcular la posición X de la línea divisoria (entre las dos columnas)
            float dividerX = startX + columnWidths[0];

            // Dibujar la línea vertical
            contentStream.moveTo(dividerX, startY);
            contentStream.lineTo(dividerX, startY - rowHeight);
            contentStream.stroke();

            // Restaurar el graphics state
            contentStream.restoreGraphicsState();
        }
    }

    @Override
    public List<String> splitTextIntoPagesOptimized(String text, PDType1Font font, float fontSize,
            float maxWidth, float firstPageHeight,
            float otherPagesHeight) throws IOException {
        List<String> pages = new ArrayList<>();

        // Dividir el texto en líneas que caben en el ancho
        List<String> allLines = splitTextIntoLines(text, font, fontSize, maxWidth);

        float lineHeight = fontSize + 3f; // Incluir espacio entre líneas

        // Calcular líneas que caben en cada tipo de página
        int linesPerFirstPage = (int) (firstPageHeight / lineHeight);
        int linesPerOtherPage = (int) (otherPagesHeight / lineHeight);

        // Asegurar mínimo de líneas
        linesPerFirstPage = Math.max(1, linesPerFirstPage);
        linesPerOtherPage = Math.max(1, linesPerOtherPage);

        if (allLines.size() <= linesPerFirstPage) {
            // Todo cabe en una página - pero necesitamos devolver el texto reconstruido de las líneas divididas
            StringBuilder singlePage = new StringBuilder();
            for (int i = 0; i < allLines.size(); i++) {
                if (singlePage.length() > 0) {
                    singlePage.append(" ");
                }
                singlePage.append(allLines.get(i));
            }
            pages.add(singlePage.toString());
            return pages;
        }

        // Primera página - usar espacio máximo disponible
        StringBuilder firstPage = new StringBuilder();
        for (int i = 0; i < linesPerFirstPage && i < allLines.size(); i++) {
            if (firstPage.length() > 0) {
                firstPage.append(" ");
            }
            firstPage.append(allLines.get(i));
        }
        pages.add(firstPage.toString());

        // Páginas siguientes - usar espacio completo de página
        int currentLine = linesPerFirstPage;
        while (currentLine < allLines.size()) {
            StringBuilder pageContent = new StringBuilder();
            int linesThisPage = 0;

            while (linesThisPage < linesPerOtherPage && currentLine < allLines.size()) {
                if (pageContent.length() > 0) {
                    pageContent.append(" ");
                }
                pageContent.append(allLines.get(currentLine));
                linesThisPage++;
                currentLine++;
            }
            pages.add(pageContent.toString());
        }

        return pages;
    }

    @Override
    public List<String> splitTextIntoPages(String text, PDType1Font font, float fontSize,
            float maxWidth, float maxHeight) throws IOException {
        List<String> pages = new ArrayList<>();

        // Primero, dividir en párrafos manteniendo la estructura
        String[] paragraphs = text.split("\n");

        float lineHeight = fontSize + 2f;
        int linesPerPage = (int) (maxHeight / lineHeight);

        if (linesPerPage <= 0) {
            linesPerPage = 1;
        }

        StringBuilder currentPage = new StringBuilder();
        int currentLineCount = 0;

        for (String paragraph : paragraphs) {
            if (paragraph.trim().isEmpty()) {
                continue;
            }

            // Dividir el párrafo en líneas que caben en el ancho
            List<String> paragraphLines = splitTextIntoLines(paragraph, font, fontSize, maxWidth);

            // Verificar si el párrafo completo cabe en la página actual
            if (currentLineCount + paragraphLines.size() > linesPerPage) {
                // No cabe, guardar página actual y empezar nueva
                if (currentPage.length() > 0) {
                    pages.add(currentPage.toString().trim());
                    currentPage = new StringBuilder();
                    currentLineCount = 0;
                }

                // Si el párrafo es muy largo para una página, dividirlo
                if (paragraphLines.size() > linesPerPage) {
                    int linesAdded = 0;
                    while (linesAdded < paragraphLines.size()) {
                        int linesToAdd = Math.min(linesPerPage, paragraphLines.size() - linesAdded);
                        for (int i = linesAdded; i < linesAdded + linesToAdd; i++) {
                            if (currentPage.length() > 0) {
                                currentPage.append(" ");
                            }
                            currentPage.append(paragraphLines.get(i));
                        }
                        pages.add(currentPage.toString().trim());
                        currentPage = new StringBuilder();
                        linesAdded += linesToAdd;
                    }
                    currentLineCount = 0;
                } else {
                    // El párrafo cabe en una página nueva
                    for (String line : paragraphLines) {
                        if (currentPage.length() > 0) {
                            currentPage.append(" ");
                        }
                        currentPage.append(line);
                    }
                    currentLineCount = paragraphLines.size();
                }
            } else {
                // Cabe en la página actual
                for (String line : paragraphLines) {
                    if (currentPage.length() > 0) {
                        currentPage.append(" ");
                    }
                    currentPage.append(line);
                }
                currentLineCount += paragraphLines.size();
            }
        }

        // Agregar la última página si queda contenido
        if (currentPage.length() > 0) {
            pages.add(currentPage.toString().trim());
        }

        return pages;
    }

    @Override
    public void drawCustomRowPages(PDPageContentStream contentStream, float startX, float startY,
            float[] columnWidths, float rowHeight,
            String[] texts, PDType1Font font, float fontSize, boolean centered, boolean[] coloredCells,
            float red, float green, float blue) throws IOException {

        float currentX = startX;
        float currentY = startY;

        contentStream.setStrokingColor(0f, 0f, 0f);
        contentStream.setLineWidth(0.5f);

        // Determinar si el color de fondo es claro u oscuro (para decidir color del texto)
        boolean isLightBackground = (red > 0.6f && green > 0.6f && blue > 0.6f);

        // Usar el array coloredCells recibido por parámetro
        for (int i = 0; i < columnWidths.length; i++) {
            String text = i < texts.length ? texts[i] : "";

            // Aplicar fondo con color personalizado si la celda está marcada en coloredCells
            if (coloredCells != null && i < coloredCells.length && coloredCells[i]) {
                contentStream.setNonStrokingColor(red, green, blue);
                contentStream.addRect(currentX, currentY - rowHeight, columnWidths[i], rowHeight);
                contentStream.fill();
            }

            // Dibujar borde de la celda
            contentStream.addRect(currentX, currentY - rowHeight, columnWidths[i], rowHeight);
            contentStream.stroke();

            // Dibujar texto si existe
            if (text != null && !text.trim().isEmpty()) {
                text = text.replace("\n", " ").replace("\r", " ").trim();
                contentStream.setFont(font, fontSize);

                // Para celdas coloreadas: texto blanco si fondo oscuro, negro si fondo claro
                if (coloredCells != null && i < coloredCells.length && coloredCells[i]) {
                    if (isLightBackground) {
                        contentStream.setNonStrokingColor(0f, 0f, 0f); // Texto negro para fondos claros
                    } else {
                        contentStream.setNonStrokingColor(1f, 1f, 1f); // Texto blanco para fondos oscuros
                    }
                } else {
                    contentStream.setNonStrokingColor(0f, 0f, 0f); // Texto negro para celdas sin color
                }

                // AUMENTAR PADDING HORIZONTAL (de 4 a 10)
                List<String> lines = splitTextIntoLines(text, font, fontSize, columnWidths[i] - 10);

                float lineHeight = fontSize + 3f; // Aumentar espacio entre líneas
                float totalTextHeight = lines.size() * lineHeight;

                // Posición vertical centrada con MÁS PADDING
                float startTextY = currentY - ((rowHeight - totalTextHeight) / 2) - (lineHeight * 0.5f);

                for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                    String line = lines.get(lineIndex);
                    float textWidth = getTextWidth(line, font, fontSize);

                    float textX;

                    if (centered) {
                        textX = currentX + (columnWidths[i] - textWidth) / 2;
                    } else {
                        // AUMENTAR PADDING IZQUIERDO (de 2 a 6)
                        textX = currentX + 6;
                    }

                    float textY = startTextY - (lineIndex * lineHeight);

                    contentStream.beginText();
                    contentStream.newLineAtOffset(textX, textY);
                    contentStream.showText(line);
                    contentStream.endText();
                }
            }

            currentX += columnWidths[i];
        }
    }

    private List<String> splitTextIntoLines(String text, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return lines;
        }

        // First, split by newlines to preserve intentional line breaks
        // Handle different line break formats: \r\n (Windows), \n (Unix), \r (Mac)
        String[] paragraphs = text.split("\\r?\\n");

        // Process each paragraph separately
        for (String paragraph : paragraphs) {
            // Remove other control characters but keep the structure
            String sanitizedParagraph = paragraph
                    .replaceAll("\\t", "    ") // Replace tabs with 4 spaces
                    .replaceAll("[\\p{Cntrl}]", ""); // Remove other control chars

            // Trim and skip empty lines (don't add empty lines to reduce spacing)
            sanitizedParagraph = sanitizedParagraph.trim();
            if (sanitizedParagraph.isEmpty()) {
                // Skip empty lines to avoid excessive spacing
                continue;
            }

            // Split paragraph into words and fit them into lines
            String[] words = sanitizedParagraph.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                if (word.isEmpty()) {
                    continue;
                }

                // VERIFICACIÓN CRÍTICA: ¿La palabra individual es más ancha que el máximo?
                float wordWidth = getTextWidth(word, font, fontSize);

                if (wordWidth > maxWidth) {
                    // CASO 1: Palabra individual demasiado larga
                    if (currentLine.length() > 0) {
                        // Guardar la línea actual antes de procesar la palabra larga
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder();
                    }

                    // Dividir la palabra larga usando TU método existente splitLongWord
                    List<String> wordParts = splitLongWord(word, font, fontSize, maxWidth);

                    // Procesar cada parte de la palabra dividida
                    for (int i = 0; i < wordParts.size(); i++) {
                        String part = wordParts.get(i);
                        float partWidth = getTextWidth(part, font, fontSize);

                        if (partWidth <= maxWidth) {
                            // La parte cabe en una línea
                            if (currentLine.length() == 0) {
                                currentLine.append(part);
                            } else {
                                // Verificar si cabe en la línea actual
                                String testLine = currentLine.toString() + " " + part;
                                float testWidth = getTextWidth(testLine, font, fontSize);

                                if (testWidth <= maxWidth) {
                                    currentLine.append(" ").append(part);
                                } else {
                                    lines.add(currentLine.toString());
                                    currentLine = new StringBuilder(part);
                                }
                            }
                        } else {
                            // La parte sigue siendo demasiado larga (caso extremo)
                            // Forzar división carácter por carácter
                            List<String> forcedParts = forceSplitWord(part, font, fontSize, maxWidth);
                            for (String forcedPart : forcedParts) {
                                if (currentLine.length() > 0) {
                                    lines.add(currentLine.toString());
                                    currentLine = new StringBuilder();
                                }
                                lines.add(forcedPart);
                            }
                        }
                    }
                } else {
                    // CASO 2: Palabra normal que debería caber
                    String testLine = currentLine.length() > 0
                            ? currentLine.toString() + " " + word
                            : word;
                    float testWidth = getTextWidth(testLine, font, fontSize);

                    if (testWidth <= maxWidth) {
                        // Cabe en la línea actual
                        currentLine = new StringBuilder(testLine);
                    } else {
                        // No cabe, guardar línea actual y empezar nueva
                        if (currentLine.length() > 0) {
                            lines.add(currentLine.toString());
                        }
                        currentLine = new StringBuilder(word);
                    }
                }
            }

            // Add the last line of this paragraph
            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }
        }

        return lines;
    }

    private List<String> forceSplitWord(String word, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        List<String> parts = new ArrayList<>();

        if (word == null || word.isEmpty()) {
            return parts;
        }

        int start = 0;
        int end = word.length();

        while (start < end) {
            // Encontrar la subcadena más larga que quepa en el ancho máximo
            int splitPoint = findOptimalSplitPoint(word, start, font, fontSize, maxWidth);

            if (splitPoint <= start) {
                // Caso de seguridad: si no podemos dividir, tomamos al menos un carácter
                splitPoint = start + 1;
            }

            String part = word.substring(start, splitPoint);
            parts.add(part);
            start = splitPoint;

            // Si hay un guion o slash después del punto de división, incluirlo en esta parte
            if (start < end && (word.charAt(start) == '-' || word.charAt(start) == '/' || word.charAt(start) == '.')) {
                parts.set(parts.size() - 1, parts.get(parts.size() - 1) + word.charAt(start));
                start++;
            }
        }

        return parts;
    }

    private int findOptimalSplitPoint(String word, int start, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        int low = start + 1;
        int high = word.length();
        int optimal = start + 1;

        // Búsqueda binaria para encontrar el punto de división óptimo
        while (low <= high) {
            int mid = low + (high - low) / 2;
            String testPart = word.substring(start, mid);
            float testWidth = getTextWidth(testPart, font, fontSize);

            if (testWidth <= maxWidth) {
                // Esta parte cabe, intentamos una más larga
                optimal = mid;
                low = mid + 1;
            } else {
                // Esta parte no cabe, intentamos una más corta
                high = mid - 1;
            }
        }

        // Si no encontramos ninguna parte que quepa, forzamos al menos un carácter
        if (optimal <= start) {
            return start + 1;
        }

        // Intentamos encontrar un punto de división natural (guion, slash, etc.)
        for (int i = optimal; i > start + 1; i--) {
            char c = word.charAt(i - 1);
            if (c == '-' || c == '/' || c == '.' || c == ' ' || Character.isUpperCase(c)) {
                // Preferimos dividir en estos caracteres
                return i;
            }
        }

        return optimal;
    }

    private float getTextWidth(String text, PDType1Font font, float fontSize) throws IOException {
        return font.getStringWidth(text) / 1000 * fontSize;
    }

// Método para dividir palabras muy largas
    private List<String> splitLongWord(String word, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        List<String> parts = new ArrayList<>();

        if (getTextWidth(word, font, fontSize) <= maxWidth) {
            parts.add(word);
            return parts;
        }

        // Buscar puntos naturales de división en español
        int[] breakPoints = {
            word.indexOf("POR"),
            word.indexOf("DE"),
            word.indexOf("ARMA"),
            word.indexOf("VIA"),
            word.indexOf("CIÓN"),
            word.indexOf("ACIÓN"),
            word.indexOf("IDAD"),
            word.indexOf("MENTO")
        };

        int bestBreak = -1;
        for (int breakPoint : breakPoints) {
            if (breakPoint > 0 && breakPoint < word.length() - 2) {
                String part1 = word.substring(0, breakPoint);
                if (getTextWidth(part1, font, fontSize) <= maxWidth) {
                    bestBreak = breakPoint;
                    break;
                }
            }
        }

        if (bestBreak != -1) {
            parts.add(word.substring(0, bestBreak));
            String remaining = word.substring(bestBreak);
            if (getTextWidth(remaining, font, fontSize) > maxWidth) {
                parts.addAll(splitLongWord(remaining, font, fontSize, maxWidth));
            } else {
                parts.add(remaining);
            }
        } else {
            // División por sílabas aproximadas
            int splitPoint = word.length() / 2;
            for (int i = splitPoint; i < word.length(); i++) {
                if (isVowel(word.charAt(i)) && i > 1) {
                    splitPoint = i;
                    break;
                }
            }

            String part1 = word.substring(0, splitPoint);
            String part2 = word.substring(splitPoint);

            parts.add(part1);
            if (getTextWidth(part2, font, fontSize) > maxWidth) {
                parts.addAll(splitLongWord(part2, font, fontSize, maxWidth));
            } else {
                parts.add(part2);
            }
        }

        return parts;
    }
//
//    private List<String> forceSplitWord(String word, PDType1Font font, float fontSize, float maxWidth) throws IOException {
//        List<String> parts = new ArrayList<>();
//        StringBuilder currentPart = new StringBuilder();
//
//        for (int i = 0; i < word.length(); i++) {
//            char c = word.charAt(i);
//            String testPart = currentPart.toString() + c;
//            float testWidth = getTextWidth(testPart, font, fontSize);
//
//            if (testWidth <= maxWidth) {
//                currentPart.append(c);
//            } else {
//                if (currentPart.length() > 0) {
//                    parts.add(currentPart.toString());
//                }
//                currentPart = new StringBuilder(String.valueOf(c));
//
//                // Verificar si el carácter individual es más ancho que el máximo
//                float charWidth = getTextWidth(String.valueOf(c), font, fontSize);
//                if (charWidth > maxWidth) {
//                    // Carácter individual demasiado ancho - forzar y continuar
//                    parts.add(String.valueOf(c));
//                    currentPart = new StringBuilder();
//                }
//            }
//        }
//
//        if (currentPart.length() > 0) {
//            parts.add(currentPart.toString());
//        }
//
//        return parts;
//    }

    private boolean isVowel(char c) {
        return "aeiouAEIOUáéíóúÁÉÍÓÚ".indexOf(c) != -1;
    }

    @Override
    public float calculateRowHeight(String[] texts, float[] columnWidths, PDType1Font font, float fontSize) throws IOException {
        float maxLines = 1;
        float lineSpacing = 7f;

        for (int i = 0; i < texts.length; i++) {
            if (texts[i] != null) {
                float maxWidth = columnWidths[i] - 8;
                List<String> lines = wrapText(texts[i], font, maxWidth, fontSize);
                maxLines = Math.max(maxLines, lines.size());
            }
        }

        return maxLines * lineSpacing + 4; // Aumentado el padding para mejor espaciado
    }

    @Override
    public float calculateRowHeightPerson(String[] texts, float[] columnWidths, PDType1Font font, float fontSize) throws IOException {
        float maxLines = 1;
        float lineSpacing = 14f;

        for (int i = 0; i < texts.length; i++) {
            if (texts[i] != null) {
                float maxWidth = columnWidths[i] - 8;
                List<String> lines = wrapText(texts[i], font, maxWidth, fontSize);
                maxLines = Math.max(maxLines, lines.size());
            }
        }

        return maxLines * lineSpacing + 6; // Aumentado el padding para mejor espaciado
    }

    @Override
    public float calculateRowHeightPersonSinBordes(String[] texts, float[] columnWidths, PDType1Font font, float fontSize) throws IOException {
        float maxLines = 1;
        float lineSpacing = 14f;

        for (int i = 0; i < texts.length; i++) {
            if (texts[i] != null) {
                float maxWidth = columnWidths[i] - 8;
                List<String> lines = wrapText(texts[i], font, maxWidth, fontSize);
                maxLines = Math.max(maxLines, lines.size());
            }
        }

        return maxLines * lineSpacing - 2f; // Disminuyendo el padding para mejor espaciado
    }

    @Override
    public float calculateRowHeightPersonSinBordesMedications(String[] texts, float[] columnWidths, PDType1Font font, float fontSize) throws IOException {
        float maxLines = 1;
        float lineSpacing = 14f;

        for (int i = 0; i < texts.length; i++) {
            if (texts[i] != null) {
                float maxWidth = columnWidths[i] - 8;
                List<String> lines = wrapText(texts[i], font, maxWidth, fontSize);
                maxLines = Math.max(maxLines, lines.size());
            }
        }

        return maxLines * lineSpacing + 1f; // Disminuyendo el padding para mejor espaciado
    }

    @Override
    public float calculateRowHeightPersonDiagnosis2Cell(String[] texts, float[] columnWidths, PDType1Font font, float fontSize) throws IOException {
        float maxLines = 1;
        float lineSpacing = 20f;

        for (int i = 0; i < texts.length; i++) {
            if (texts[i] != null) {
                float maxWidth = columnWidths[i] - 8;
                List<String> lines = wrapText(texts[i], font, maxWidth, fontSize);
                maxLines = Math.max(maxLines, lines.size());
            }
        }

        return maxLines * lineSpacing + 12; // Aumentado el padding para mejor espaciado
    }

    private List<String> wrapText(String text, PDType1Font font, float maxWidth, float fontSize) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return lines;
        }

        // Split by newlines first to preserve intentional line breaks
        String[] paragraphs = text.split("\\r?\\n");

        for (String paragraph : paragraphs) {
            // Remove control characters but keep structure
            String sanitizedParagraph = paragraph
                    .replaceAll("\\t", "    ")
                    .replaceAll("[\\p{Cntrl}]", "");

            sanitizedParagraph = sanitizedParagraph.trim();
            if (sanitizedParagraph.isEmpty()) {
                continue;
            }

            String[] words = sanitizedParagraph.split("\\s+");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                if (word.isEmpty()) {
                    continue;
                }

                // Verificar el ancho de la palabra individual
                float wordWidth = getTextWidth(word, font, fontSize);

                if (wordWidth > maxWidth) {
                    // Palabra demasiado larga
                    if (currentLine.length() > 0) {
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder();
                    }

                    // Dividir la palabra larga usando splitLongWord
                    List<String> wordParts = splitLongWord(word, font, fontSize, maxWidth);

                    // Procesar cada parte de la palabra dividida
                    for (String part : wordParts) {
                        float partWidth = getTextWidth(part, font, fontSize);

                        if (partWidth <= maxWidth) {
                            // La parte cabe en una línea
                            if (currentLine.length() == 0) {
                                currentLine.append(part);
                            } else {
                                String testLine = currentLine.toString() + " " + part;
                                float testWidth = getTextWidth(testLine, font, fontSize);

                                if (testWidth <= maxWidth) {
                                    currentLine.append(" ").append(part);
                                } else {
                                    lines.add(currentLine.toString());
                                    currentLine = new StringBuilder(part);
                                }
                            }
                        } else {
                            // La parte sigue siendo demasiado larga (caso extremo)
                            // Forzar división carácter por carácter
                            List<String> forcedParts = forceSplitWord(part, font, fontSize, maxWidth);
                            for (String forcedPart : forcedParts) {
                                if (currentLine.length() > 0) {
                                    lines.add(currentLine.toString());
                                    currentLine = new StringBuilder();
                                }
                                lines.add(forcedPart);
                            }
                        }
                    }
                } else {
                    // Palabra normal
                    String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;
                    float testWidth = getTextWidth(testLine, font, fontSize);

                    if (testWidth <= maxWidth) {
                        currentLine = new StringBuilder(testLine);
                    } else {
                        if (currentLine.length() > 0) {
                            lines.add(currentLine.toString());
                        }
                        currentLine = new StringBuilder(word);
                    }
                }
            }

            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }
        }

        return lines;
    }

    private int findOptimalSplitIndex(String word, float maxWidth, PDType1Font font, float fontSize) throws IOException {
        int low = 1;
        int high = word.length();
        int splitIndex = word.length() / 2;

        // Búsqueda binaria para encontrar el mejor punto de división
        while (low <= high) {
            int mid = low + (high - low) / 2;
            float width = getTextWidth(word.substring(0, mid), font, fontSize);

            if (width <= maxWidth) {
                splitIndex = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return Math.max(1, splitIndex);
    }

    /**
     * MÉTODO PARA SALTO DE PÁGINA AUTOMÁTICO
     *
     * @param document
     * @param currentContentStream
     * @return
     * @throws IOException
     */
    @Override
    public PDPageContentStream createNewPage(PDDocument document, PDPageContentStream currentContentStream) throws IOException {
        // Cerrar el contentStream actual si existe
        if (currentContentStream != null) {
            currentContentStream.close();
        }

        // Crear nueva página
        PDPage newPage = new PDPage(PDRectangle.A4);
        document.addPage(newPage);

        // Crear y devolver nuevo contentStream
        return new PDPageContentStream(document, newPage);
    }

    @Override
    public void drawBase64Image(PDDocument document, PDPageContentStream contentStream,
            String base64Image, float x, float y, float width, float height) throws IOException {
        try {
            // Remover el prefijo si existe (data:image/png;base64,...)
            String base64Data = base64Image;
            if (base64Image.contains(",")) {
                base64Data = base64Image.split(",")[1];
            }

            // Decodificar la imagen Base64
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            // Crear el objeto imagen usando el documento
            PDImageXObject image = PDImageXObject.createFromByteArray(
                    document,
                    imageBytes,
                    "embedded-image"
            );

            // Dibujar la imagen
            contentStream.drawImage(image, x, y, width, height);

        } catch (Exception e) {
            System.err.println("Error al dibujar imagen Base64: " + e.getMessage());
            // Opcional: dibujar un rectángulo rojo como fallback
            contentStream.setNonStrokingColor(1f, 0f, 0f); // Rojo en RGB normalizado
            contentStream.addRect(x, y, width, height);
            contentStream.fill();
            contentStream.setNonStrokingColor(0f, 0f, 0f); // Volver a negro
        }
    }
}
