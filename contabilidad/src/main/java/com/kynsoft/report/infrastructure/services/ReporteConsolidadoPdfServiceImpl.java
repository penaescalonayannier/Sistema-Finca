package com.kynsoft.report.infrastructure.services;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.kynsoft.report.domain.dto.ReporteConsolidadoPdfDto;
import com.kynsoft.report.domain.dto.TrabajadorConsolidadoDto;
import com.kynsoft.report.domain.services.IReporteConsolidadoPdfService;
import org.springframework.stereotype.Service;

import com.itextpdf.layout.element.AreaBreak;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteConsolidadoPdfServiceImpl implements IReporteConsolidadoPdfService {

    private static final int MAX_TRABAJADORES_POR_PAGINA = 25; // Aumentado porque las filas son más compactas

    private static final Map<String, String> CARGO_ABREVIATURAS = new HashMap<>();
    
    static {
        CARGO_ABREVIATURAS.put("Jefe de Finca Agropecuaria", "Jefe Finca");
        CARGO_ABREVIATURAS.put("Especialista A en Producción de Caña", "Esp. Prod. Caña");
        CARGO_ABREVIATURAS.put("Especialista A en Producción Agropecuaria", "Esp. Prod. Agropec.");
        CARGO_ABREVIATURAS.put("Gestor Administrativo", "Gestor Adm.");
        CARGO_ABREVIATURAS.put("Técnico en Mecanización Agrícola", "Téc. Mec. Agrícola");
        CARGO_ABREVIATURAS.put("Técnico en Producción Agropecuaria", "Téc. Prod. Agropec.");
        CARGO_ABREVIATURAS.put("Inspector B de Campo", "Insp. Campo");
        CARGO_ABREVIATURAS.put("Técnico en Abastecimiento Técnico Material", "Téc. Abast. Téc. Mat.");
        CARGO_ABREVIATURAS.put("Contador D", "Contador D");
        CARGO_ABREVIATURAS.put("Mecánico B Automotor", "Mec. B Automotor");
        CARGO_ABREVIATURAS.put("Sereno (Finca)", "Sereno");
        CARGO_ABREVIATURAS.put("Cocinero Integral \"C\"", "Cocinero C");
        CARGO_ABREVIATURAS.put("Operario Agropecuario Especializado (Caña, Cultivos Varios ó Pecuario)", "Op. Agropec. Esp.");
        CARGO_ABREVIATURAS.put("Jefe de Brigada de Producción Agropecuaria", "Jefe Brig. Prod.");
        CARGO_ABREVIATURAS.put("Operador de Tractor sobre Neumático con Aditamentos (finca)", "Op. Tractor");
        CARGO_ABREVIATURAS.put("Mecánico A Automotor(Jefe de Brigada)", "Mec. A Automotor");
        CARGO_ABREVIATURAS.put("Operador Mecánico de Combinadas Cañeras", "Op. Combinadas");
        CARGO_ABREVIATURAS.put("Operador de Tractor sobre Neumáticos con Aditamentos (Finca)", "Op. Tractor");
        CARGO_ABREVIATURAS.put("Mecánico de Combinadas Cañeras", "Mec. Combinadas");
        CARGO_ABREVIATURAS.put("Operario Agropecuario Especializado (Computador-Enganchador)", "Op. Computador");
        CARGO_ABREVIATURAS.put("Soldador \"B\"", "Soldador B");
    }

    @Override
    public byte[] generarPdfConsolidado(ReporteConsolidadoPdfDto data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);

            pdfDoc.setDefaultPageSize(PageSize.LETTER.rotate());

            Document document = new Document(pdfDoc);
            document.setMargins(15, 15, 15, 15);

            PdfFont font = PdfFontFactory.createFont("Helvetica");
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");

            List<TrabajadorConsolidadoDto> trabajadores = data.getTrabajadores();
            if (trabajadores == null) {
                trabajadores = new ArrayList<>();
            }
            int diasDelMes = data.getDiasDelMes();

            List<List<TrabajadorConsolidadoDto>> paginas = dividirTrabajadoresEnPaginas(trabajadores);

            // Si no hay trabajadores, mostrar mensaje
            if (paginas.isEmpty()) {
                Paragraph title = new Paragraph("REPORTE DE TIEMPO DE TRABAJO")
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setUnderline()
                        .setMarginBottom(3);
                document.add(title);

                Paragraph periodo = new Paragraph("Mes: " + data.getMes() + " - Año: " + data.getYear())
                        .setFont(boldFont)
                        .setFontSize(9)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20);
                document.add(periodo);

                Paragraph mensaje = new Paragraph("No hay datos de trabajadores para este período")
                        .setFont(font)
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER);
                document.add(mensaje);

                document.close();
                return baos.toByteArray();
            }

            int numeroConsecutivo = 1; // Contador global para numeración consecutiva

            // TÍTULO y PERÍODO una sola vez al inicio
            Paragraph title = new Paragraph("REPORTE DE TIEMPO DE TRABAJO")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setUnderline()
                    .setMarginBottom(3);
            document.add(title);

            Paragraph periodo = new Paragraph("Mes: " + data.getMes() + " - Año: " + data.getYear())
                    .setFont(boldFont)
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            document.add(periodo);

            for (int paginaIdx = 0; paginaIdx < paginas.size(); paginaIdx++) {
                List<TrabajadorConsolidadoDto> trabajadoresPagina = paginas.get(paginaIdx);
                
                // TABLA PRINCIPAL - Columnas más ajustadas
                float[] columnWidths = new float[2 + diasDelMes + 1];
                columnWidths[0] = 0.08f; // N°
                columnWidths[1] = 4.0f; // Nombre + Cargo (en línea)
                for (int i = 0; i < diasDelMes; i++) {
                    columnWidths[2 + i] = 0.6f; // Días (más pequeños)
                }
                columnWidths[columnWidths.length - 1] = 1.0f; // Total
                
                Table table = new Table(UnitValue.createPercentArray(columnWidths))
                        .setWidth(UnitValue.createPercentValue(100))
                        .setFontSize(6);
                
                // Encabezados - Celdas que abarcan 2 filas
                Cell headerN = new Cell(2, 1)
                        .add(new Paragraph("N°").setFont(boldFont).setFontSize(5))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(1);
                table.addHeaderCell(headerN);

                Cell headerNombre = new Cell(2, 1)
                        .add(new Paragraph("NOMBRE Y CARGO").setFont(boldFont).setFontSize(5))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(1);
                table.addHeaderCell(headerNombre);

                // Agregar iniciales de días de semana (Fila 1)
                int yearInt = Integer.parseInt(data.getYear());
                int mesInt = getMonthNumber(data.getMes());
                for (int dia = 1; dia <= diasDelMes; dia++) {
                    LocalDate fecha = LocalDate.of(yearInt, mesInt, dia);
                    String diaSemana = getDiaSemanaBrev(fecha.getDayOfWeek().getValue());
                    Cell diaHeader = new Cell()
                            .add(new Paragraph(diaSemana).setFont(boldFont).setFontSize(4))
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(1);
                    table.addHeaderCell(diaHeader);
                }

                Cell headerTotal = new Cell(2, 1)
                        .add(new Paragraph("TOTAL").setFont(boldFont).setFontSize(5))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(1);
                table.addHeaderCell(headerTotal);

                // Números de días (Fila 2)
                for (int dia = 1; dia <= diasDelMes; dia++) {
                    Cell diaHeader = new Cell()
                            .add(new Paragraph(String.valueOf(dia)).setFont(boldFont).setFontSize(5))
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(1);
                    table.addHeaderCell(diaHeader);
                }
                
                // Datos
                for (int i = 0; i < trabajadoresPagina.size(); i++) {
                    TrabajadorConsolidadoDto trabajador = trabajadoresPagina.get(i);
                    Map<Integer, String> horasPorDia = trabajador.getHorasPorDia();
                    if (horasPorDia == null) {
                        horasPorDia = new HashMap<>();
                    }

                    // N° - más compacto - Numeración consecutiva global
                    table.addCell(new Cell()
                            .add(new Paragraph(String.valueOf(numeroConsecutivo)).setFont(font).setFontSize(5))
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(1));
                    numeroConsecutivo++;
                    
                    // ========== NOMBRE Y CARGO EN LA MISMA LÍNEA ==========
                    String nombreCompleto = trabajador.getNombre() != null ? trabajador.getNombre() : "Sin nombre";
                    String cargoOriginal = trabajador.getCargo() != null ? trabajador.getCargo() : "";
                    String cargoAbreviado = getCargoAbreviado(cargoOriginal);
                    
                    // Crear un Paragraph con nombre y cargo en la misma línea
                    Paragraph nombreCargo = new Paragraph()
                            .add(new Text(nombreCompleto).setFont(boldFont).setFontSize(6))
                            .add(new Text(" - ").setFont(font).setFontSize(5))
                            .add(new Text(cargoAbreviado).setFont(font).setFontSize(5).setFontColor(ColorConstants.GRAY));
                    
                    table.addCell(new Cell()
                            .add(nombreCargo)
                            .setPadding(1));
                    
                    // Días del mes
                    int totalHoras = 0;
                    for (int dia = 1; dia <= diasDelMes; dia++) {
                        String horas = horasPorDia.getOrDefault(dia, "");
                        table.addCell(new Cell()
                                .add(new Paragraph(horas).setFont(font).setFontSize(5))
                                .setTextAlignment(TextAlignment.CENTER)
                                .setPadding(1));

                        if (horas != null && !horas.isEmpty()) {
                            try {
                                totalHoras += Integer.parseInt(horas);
                            } catch (NumberFormatException e) {
                                // Ignorar
                            }
                        }
                    }
                    
                    // Total
                    table.addCell(new Cell()
                            .add(new Paragraph(String.valueOf(totalHoras)).setFont(boldFont).setFontSize(6))
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(1));
                }
                
                // Fila de totales
                Cell totalLabelCell = new Cell(1, 2)
                        .add(new Paragraph("TOTAL").setFont(boldFont).setFontSize(5))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(1);
                table.addCell(totalLabelCell);
                
                // Totales por día
                for (int dia = 1; dia <= diasDelMes; dia++) {
                    int totalDia = 0;
                    for (TrabajadorConsolidadoDto trabajador : trabajadoresPagina) {
                        Map<Integer, String> horasPorDia = trabajador.getHorasPorDia();
                        if (horasPorDia == null) {
                            horasPorDia = new HashMap<>();
                        }
                        String horas = horasPorDia.getOrDefault(dia, "");
                        if (horas != null && !horas.isEmpty()) {
                            try {
                                totalDia += Integer.parseInt(horas);
                            } catch (NumberFormatException e) {
                                // Ignorar
                            }
                        }
                    }
                    table.addCell(new Cell()
                            .add(new Paragraph(String.valueOf(totalDia)).setFont(boldFont).setFontSize(5))
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(1));
                }
                
                // Total general
                int totalGeneral = 0;
                for (TrabajadorConsolidadoDto trabajador : trabajadoresPagina) {
                    totalGeneral += trabajador.getTotalHoras() != null ? trabajador.getTotalHoras() : 0;
                }
                table.addCell(new Cell()
                        .add(new Paragraph(String.valueOf(totalGeneral)).setFont(boldFont).setFontSize(5))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(1));
                
                document.add(table);

                // PIE DE PÁGINA
                Paragraph footer = new Paragraph(
                        "Página " + (paginaIdx + 1) + " de " + paginas.size() +
                        " | Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                )
                        .setFont(font)
                        .setFontSize(5)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(5);
                document.add(footer);
            }

            // Cerrar el documento solo una vez al final
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del consolidado: " + e.getMessage(), e);
        }
    }
    
    private List<List<TrabajadorConsolidadoDto>> dividirTrabajadoresEnPaginas(List<TrabajadorConsolidadoDto> trabajadores) {
        List<List<TrabajadorConsolidadoDto>> paginas = new ArrayList<>();
        for (int i = 0; i < trabajadores.size(); i += MAX_TRABAJADORES_POR_PAGINA) {
            int end = Math.min(i + MAX_TRABAJADORES_POR_PAGINA, trabajadores.size());
            paginas.add(trabajadores.subList(i, end));
        }
        return paginas;
    }
    
    private String getCargoAbreviado(String cargo) {
        if (cargo == null || cargo.isEmpty()) {
            return "";
        }
        return CARGO_ABREVIATURAS.getOrDefault(cargo, cargo);
    }

    private int getMonthNumber(String mes) {
        Map<String, Integer> meses = new HashMap<>();
        meses.put("Enero", 1);
        meses.put("Febrero", 2);
        meses.put("Marzo", 3);
        meses.put("Abril", 4);
        meses.put("Mayo", 5);
        meses.put("Junio", 6);
        meses.put("Julio", 7);
        meses.put("Agosto", 8);
        meses.put("Septiembre", 9);
        meses.put("Octubre", 10);
        meses.put("Noviembre", 11);
        meses.put("Diciembre", 12);
        return meses.getOrDefault(mes, 1);
    }

    private String getDiaSemanaBrev(int dayOfWeek) {
        // dayOfWeek: 1=Lunes, 2=Martes, ..., 7=Domingo
        switch (dayOfWeek) {
            case 1: return "L";
            case 2: return "M";
            case 3: return "X";
            case 4: return "J";
            case 5: return "V";
            case 6: return "S";
            case 7: return "D";
            default: return "";
        }
    }
}