package com.kynsoft.report.infrastructure.services;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.kynsoft.report.domain.dto.DiaTrabajoDto;
import com.kynsoft.report.domain.dto.ReporteDto;
import com.kynsoft.report.domain.dto.ReportePdfDto;
import com.kynsoft.report.domain.dto.TrabajadorDiaDto;
import com.kynsoft.report.domain.services.IReportePdfService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportePdfServiceImpl implements IReportePdfService {

    @Override
    public byte[] generarPdfReporte(ReportePdfDto data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Configurar fuentes
            PdfFont font = PdfFontFactory.createFont("Helvetica");
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");

            // ==================== TÍTULO ====================
            Paragraph title = new Paragraph("REPORTE DE TIEMPO DE TRABAJO")
                    .setFont(boldFont)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setUnderline()
                    .setMarginBottom(15);
            document.add(title);

            // ==================== INFORMACIÓN DEL REPORTE ====================
            ReporteDto reporte = data.getReporte();
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(15);

            addInfoRow(infoTable, "Código:", reporte.getCodigo(), boldFont, font);
            addInfoRow(infoTable, "Bloque:", reporte.getBloque(), boldFont, font);
            addInfoRow(infoTable, "Campo:", reporte.getCampo(), boldFont, font);
            addInfoRow(infoTable, "Área:", reporte.getArea(), boldFont, font);
            addInfoRow(infoTable, "Norma:", reporte.getNorma(), boldFont, font);
            addInfoRow(infoTable, "Año/Mes:", reporte.getYear() + " / " + reporte.getMes(), boldFont, font);

            document.add(infoTable);

            // Línea separadora
            Table separator = new Table(UnitValue.createPercentArray(new float[]{1}))
                    .setWidth(UnitValue.createPercentValue(100));
            Cell separatorCell = new Cell()
                    .setBorderBottom(Border.NO_BORDER)
                    .setBorderTop(Border.NO_BORDER)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setHeight(1)
                    .setPadding(0);
            separator.addCell(separatorCell);
            document.add(separator);

            // ==================== TABLA DE DÍAS Y TRABAJADORES ====================
            List<DiaTrabajoDto> dias = data.getDias();
            
            if (dias != null && !dias.isEmpty()) {
                // Ordenar días por fecha
                dias.sort(Comparator.comparing(DiaTrabajoDto::getFecha));
                
                // Para cada día, mostrar una tabla
                for (DiaTrabajoDto dia : dias) {
                    // Título del día
                    Paragraph diaTitle = new Paragraph("Fecha: " + dia.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .setFont(boldFont)
                            .setFontSize(11)
                            .setMarginTop(10)
                            .setMarginBottom(5);
                    document.add(diaTitle);
                    
                    // Tabla de trabajadores del día
                    if (dia.getTrabajadores() != null && !dia.getTrabajadores().isEmpty()) {
                        Table trabajadoresTable = new Table(UnitValue.createPercentArray(new float[]{3, 2, 1.5f, 1.5f}))
                                .setWidth(UnitValue.createPercentValue(100));

                        // Encabezados
                        String[] headers = {"Trabajador", "RUC", "Norma", "Horas"};
                        for (String header : headers) {
                            Cell headerCell = new Cell()
                                    .add(new Paragraph(header).setFont(boldFont).setFontSize(9))
                                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                    .setTextAlignment(TextAlignment.CENTER)
                                    .setPadding(4);
                            trabajadoresTable.addHeaderCell(headerCell);
                        }

                        // Datos
                        for (TrabajadorDiaDto td : dia.getTrabajadores()) {
                            trabajadoresTable.addCell(new Cell()
                                    .add(new Paragraph(td.getTrabajadorNombre() != null ? 
                                            td.getTrabajadorNombre() : "N/A")
                                            .setFont(font).setFontSize(8))
                                    .setPadding(3));

                            trabajadoresTable.addCell(new Cell()
                                    .add(new Paragraph(td.getTrabajadorRuc() != null ? 
                                            td.getTrabajadorRuc() : "-")
                                            .setFont(font).setFontSize(8))
                                    .setTextAlignment(TextAlignment.CENTER)
                                    .setPadding(3));

                            trabajadoresTable.addCell(new Cell()
                                    .add(new Paragraph(td.getNorma() != null ? 
                                            td.getNorma() : "-")
                                            .setFont(font).setFontSize(8))
                                    .setTextAlignment(TextAlignment.CENTER)
                                    .setPadding(3));

                            trabajadoresTable.addCell(new Cell()
                                    .add(new Paragraph(td.getHoras() != null ? 
                                            td.getHoras() : "-")
                                            .setFont(font).setFontSize(8))
                                    .setTextAlignment(TextAlignment.CENTER)
                                    .setPadding(3));
                        }

                        document.add(trabajadoresTable);
                    } else {
                        Paragraph noData = new Paragraph("No hay trabajadores asignados para esta fecha")
                                .setFont(font)
                                .setFontSize(9)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginTop(5);
                        document.add(noData);
                    }
                    
                    // Espacio entre días
                    document.add(new Paragraph(" "));
                }
            } else {
                Paragraph noData = new Paragraph("No hay días registrados para este reporte")
                        .setFont(font)
                        .setFontSize(11)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(20);
                document.add(noData);
            }

            // ==================== TOTALES ====================
            // Calcular total de trabajadores únicos
            long totalTrabajadores = dias.stream()
                    .flatMap(d -> d.getTrabajadores().stream())
                    .map(TrabajadorDiaDto::getTrabajadorId)
                    .distinct()
                    .count();

            Paragraph totales = new Paragraph("Total de trabajadores únicos: " + totalTrabajadores)
                    .setFont(boldFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(15);
            document.add(totales);

            // ==================== FOOTER ====================
            Paragraph footer = new Paragraph("Generado: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .setFont(font)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del reporte", e);
        }
    }

    private void addInfoRow(Table table, String label, String value, PdfFont boldFont, PdfFont font) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setFont(boldFont).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setPadding(3);
        table.addCell(labelCell);

        Cell valueCell = new Cell()
                .add(new Paragraph(value != null ? value : "-").setFont(font).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setPadding(3);
        table.addCell(valueCell);
    }
}