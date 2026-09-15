package com.kynsoft.report.infrastructure.services;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.kynsoft.report.domain.dto.ReporteConsolidadoPorResponsablePdfDto;
import com.kynsoft.report.domain.dto.ResponsableConsolidadoDto;
import com.kynsoft.report.domain.dto.TrabajadorConsolidadoDto;
import com.kynsoft.report.domain.services.IReporteConsolidadoPorResponsablePdfService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteConsolidadoPorResponsablePdfServiceImpl implements IReporteConsolidadoPorResponsablePdfService {

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
    public byte[] generarPdfConsolidadoPorResponsable(ReporteConsolidadoPorResponsablePdfDto data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);

            pdfDoc.setDefaultPageSize(PageSize.LETTER.rotate());

            PdfFont font = PdfFontFactory.createFont("Helvetica");
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");

            List<ResponsableConsolidadoDto> responsables = data.getResponsables();
            int diasDelMes = data.getDiasDelMes();

            // Crear documento
            Document document = new Document(pdfDoc);
            document.setMargins(15, 15, 15, 15);

            // Procesar cada responsable
            for (int respIdx = 0; respIdx < responsables.size(); respIdx++) {
                ResponsableConsolidadoDto responsable = responsables.get(respIdx);
                List<TrabajadorConsolidadoDto> trabajadores = responsable.getTrabajadores();

                // Agregar salto de página si no es el primer responsable
                if (respIdx > 0) {
                    document.add(new AreaBreak());
                }

                // Encabezado de la página con título y período (una sola vez por responsable)
                Paragraph pageTitle = new Paragraph("REPORTE DE TIEMPO DE TRABAJO POR RESPONSABLE")
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setUnderline()
                        .setMarginBottom(3);
                document.add(pageTitle);

                Paragraph pagePeriodo = new Paragraph("Mes: " + data.getMes() + " - Año: " + data.getYear())
                        .setFont(boldFont)
                        .setFontSize(9)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(10);
                document.add(pagePeriodo);

                // Encabezado de Responsable
                Paragraph responsableHeader = new Paragraph("Responsable: " + responsable.getTrabajadorResponsableNombre())
                        .setFont(boldFont)
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMarginTop(5)
                        .setMarginBottom(5);
                document.add(responsableHeader);

                // Tabla para este responsable
                float[] columnWidths = new float[2 + diasDelMes + 1];
                columnWidths[0] = 0.08f; // N°
                columnWidths[1] = 4.0f; // Nombre + Cargo
                for (int i = 0; i < diasDelMes; i++) {
                    columnWidths[2 + i] = 0.6f; // Días
                }
                columnWidths[columnWidths.length - 1] = 1.0f; // Total

                Table table = new Table(UnitValue.createPercentArray(columnWidths))
                        .setWidth(UnitValue.createPercentValue(100))
                        .setFontSize(6);

                // Encabezados de tabla - Fila 1 (Iniciales de días de semana)
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

                // Datos de trabajadores
                for (int i = 0; i < trabajadores.size(); i++) {
                    TrabajadorConsolidadoDto trabajador = trabajadores.get(i);
                    Map<Integer, String> horasPorDia = trabajador.getHorasPorDia();
                    if (horasPorDia == null) {
                        horasPorDia = new HashMap<>();
                    }

                    // N°
                    table.addCell(new Cell()
                            .add(new Paragraph(String.valueOf(i + 1)).setFont(font).setFontSize(5))
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(1));

                    // Nombre y Cargo
                    String nombreCompleto = trabajador.getNombre() != null ? trabajador.getNombre() : "Sin nombre";
                    String cargoOriginal = trabajador.getCargo() != null ? trabajador.getCargo() : "";
                    String cargoAbreviado = getCargoAbreviado(cargoOriginal);

                    Paragraph nombreCargo = new Paragraph()
                            .add(new Text(nombreCompleto).setFont(boldFont).setFontSize(6))
                            .add(new Text(" - ").setFont(font).setFontSize(5))
                            .add(new Text(cargoAbreviado).setFont(font).setFontSize(5).setFontColor(ColorConstants.GRAY));

                    table.addCell(new Cell()
                            .add(nombreCargo)
                            .setPadding(1));

                    // Días del mes
                    for (int dia = 1; dia <= diasDelMes; dia++) {
                        String horas = horasPorDia.getOrDefault(dia, "");
                        table.addCell(new Cell()
                                .add(new Paragraph(horas).setFont(font).setFontSize(5))
                                .setTextAlignment(TextAlignment.CENTER)
                                .setPadding(1));
                    }

                    // Total por trabajador
                    table.addCell(new Cell()
                            .add(new Paragraph(String.valueOf(trabajador.getTotalHoras() != null ? trabajador.getTotalHoras().intValue() : 0))
                                    .setFont(boldFont).setFontSize(6))
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(1));
                }

                // Fila de totales para este responsable
                Cell totalLabelCell = new Cell(1, 2)
                        .add(new Paragraph("TOTAL").setFont(boldFont).setFontSize(5))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(1);
                table.addCell(totalLabelCell);

                // Totales por día
                for (int dia = 1; dia <= diasDelMes; dia++) {
                    int totalDia = 0;
                    for (TrabajadorConsolidadoDto trabajador : trabajadores) {
                        Map<Integer, String> horasPorDia = trabajador.getHorasPorDia();
                        if (horasPorDia == null) {
                            horasPorDia = new HashMap<>();
                        }
                        String horas = horasPorDia.getOrDefault(dia, "");
                        if (horas != null && !horas.isEmpty()) {
                            totalDia += convertirHorasANumero(horas);
                        }
                    }
                    table.addCell(new Cell()
                            .add(new Paragraph(String.valueOf(totalDia)).setFont(boldFont).setFontSize(5))
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(1));
                }

                // Total general para este responsable
                int totalGeneral = 0;
                for (TrabajadorConsolidadoDto trabajador : trabajadores) {
                    totalGeneral += trabajador.getTotalHoras() != null ? trabajador.getTotalHoras().intValue() : 0;
                }
                table.addCell(new Cell()
                        .add(new Paragraph(String.valueOf(totalGeneral)).setFont(boldFont).setFontSize(5))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(1));

                document.add(table);

                // Agregar espacio antes de la firma
                document.add(new Paragraph(" ").setMarginTop(15).setMarginBottom(10));

                // Crear tabla para las firmas (sin bordes pero con contenido visible)
                Table firmaTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                        .setWidth(UnitValue.createPercentValue(100))
                        .setMarginTop(20);

                // Celda 1: Firma
                Cell firmaCell = new Cell()
                        .add(new Paragraph("\n\n").setFontSize(7))
                        .add(new Paragraph("_______________________").setFont(font).setFontSize(7).setTextAlignment(TextAlignment.CENTER))
                        .add(new Paragraph("Firma del Responsable").setFont(font).setFontSize(8).setTextAlignment(TextAlignment.CENTER))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(null)
                        .setMarginLeft(5)
                        .setMarginRight(5);
                firmaTable.addCell(firmaCell);

                // Celda 2: Nombre
                Cell nombreCell = new Cell()
                        .add(new Paragraph("\n\n").setFontSize(7))
                        .add(new Paragraph("_______________________").setFont(font).setFontSize(7).setTextAlignment(TextAlignment.CENTER))
                        .add(new Paragraph(responsable.getTrabajadorResponsableNombre()).setFont(boldFont).setFontSize(8).setTextAlignment(TextAlignment.CENTER))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(null)
                        .setMarginLeft(5)
                        .setMarginRight(5);
                firmaTable.addCell(nombreCell);

                // Celda 3: Fecha
                Cell fechaCell = new Cell()
                        .add(new Paragraph("\n\n").setFontSize(7))
                        .add(new Paragraph("_______________________").setFont(font).setFontSize(7).setTextAlignment(TextAlignment.CENTER))
                        .add(new Paragraph("Fecha").setFont(font).setFontSize(8).setTextAlignment(TextAlignment.CENTER))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(null)
                        .setMarginLeft(5)
                        .setMarginRight(5);
                firmaTable.addCell(fechaCell);

                document.add(firmaTable);

                // Agregar footer en cada página
                document.add(new Paragraph(" ").setMarginTop(15));
                Paragraph pageFooter = new Paragraph(
                        "Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                )
                        .setFont(font)
                        .setFontSize(6)
                        .setTextAlignment(TextAlignment.CENTER);
                document.add(pageFooter);
            }

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del consolidado por responsable", e);
        }
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

    private int convertirHorasANumero(String horas) {
        try {
            if (horas.contains(":")) {
                String[] partes = horas.split(":");
                int horasInt = Integer.parseInt(partes[0]);
                int minutosInt = partes.length > 1 ? Integer.parseInt(partes[1]) : 0;
                return horasInt + (minutosInt > 0 ? 1 : 0);
            } else {
                return Integer.parseInt(horas);
            }
        } catch (Exception e) {
            return 0;
        }
    }
}
