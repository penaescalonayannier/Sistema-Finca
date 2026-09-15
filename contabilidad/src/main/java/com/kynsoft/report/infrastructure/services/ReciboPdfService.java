package com.kynsoft.report.infrastructure.services;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.kynsoft.report.domain.dto.PagoDeudaDto;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class ReciboPdfService {

    private static final DeviceRgb HEADER_BG = new DeviceRgb(40, 167, 69);
    private static final DeviceRgb LIGHT_GREEN = new DeviceRgb(232, 245, 233);
    private static final DeviceRgb BORDER_COLOR = new DeviceRgb(200, 200, 200);
    private static final DeviceRgb TEXT_DARK = new DeviceRgb(51, 51, 51);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generarComprobante(PagoDeudaDto pago) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);

        // Half letter size (5.5 x 8.5 inches)
        PageSize halfLetter = new PageSize(396, 612);
        Document document = new Document(pdf, halfLetter);
        document.setMargins(20, 20, 20, 20);

        PdfFont fontBold = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont fontNormal = PdfFontFactory.createFont("Helvetica");

        // Header
        Paragraph titulo = new Paragraph("COMPROBANTE DE PAGO")
                .setFont(fontBold)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(HEADER_BG)
                .setMarginBottom(5);
        document.add(titulo);

        // Finca and Receipt number
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(10);

        headerTable.addCell(new Cell()
                .add(new Paragraph("Finca: " + (pago.getFincaName() != null ? pago.getFincaName() : ""))
                        .setFont(fontNormal).setFontSize(9))
                .setBorder(Border.NO_BORDER));
        headerTable.addCell(new Cell()
                .add(new Paragraph("No. Recibo: " + pago.getNumeroRecibo())
                        .setFont(fontBold).setFontSize(10))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell(1, 2)
                .add(new Paragraph("Fecha: " + (pago.getFecha() != null ? pago.getFecha().format(DATE_FORMATTER) : ""))
                        .setFont(fontNormal).setFontSize(9))
                .setBorder(Border.NO_BORDER));

        document.add(headerTable);

        // Separator line
        document.add(new Paragraph("").setBorderBottom(new SolidBorder(BORDER_COLOR, 1)).setMarginBottom(10));

        // Worker info box
        Table workerTable = new Table(UnitValue.createPercentArray(new float[]{100}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(10);

        Cell workerCell = new Cell()
                .setBackgroundColor(LIGHT_GREEN)
                .setBorder(new SolidBorder(HEADER_BG, 1))
                .setPadding(10);

        workerCell.add(new Paragraph("RECIBIDO DE:")
                .setFont(fontBold).setFontSize(9).setMarginBottom(5));
        workerCell.add(new Paragraph("Nombre: " + (pago.getTrabajadorNombre() != null ? pago.getTrabajadorNombre() : ""))
                .setFont(fontNormal).setFontSize(10).setMarginBottom(3));
        workerCell.add(new Paragraph("RUC/CI: " + (pago.getTrabajadorRuc() != null ? pago.getTrabajadorRuc() : ""))
                .setFont(fontNormal).setFontSize(10));

        workerTable.addCell(workerCell);
        document.add(workerTable);

        // Concept
        document.add(new Paragraph("CONCEPTO: " + (pago.getConcepto() != null ? pago.getConcepto() : "Pago de deuda"))
                .setFont(fontNormal).setFontSize(10).setMarginBottom(10));

        // Amounts table
        Table amountsTable = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(15);

        // Saldo anterior
        amountsTable.addCell(createAmountLabelCell("Saldo anterior:", fontNormal));
        amountsTable.addCell(createAmountValueCell(String.format("$ %.2f", pago.getSaldoAnterior()), fontNormal, false));

        // Monto pagado
        amountsTable.addCell(createAmountLabelCell("Monto pagado:", fontBold));
        amountsTable.addCell(createAmountValueCell(String.format("$ %.2f", pago.getMonto()), fontBold, false));

        // Separator row
        amountsTable.addCell(new Cell(1, 2)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(BORDER_COLOR, 1))
                .setPadding(5));

        // Saldo nuevo (highlighted)
        Cell labelCell = new Cell()
                .add(new Paragraph("SALDO NUEVO:")
                        .setFont(fontBold).setFontSize(11))
                .setBackgroundColor(HEADER_BG)
                .setFontColor(ColorConstants.WHITE)
                .setBorder(new SolidBorder(HEADER_BG, 1))
                .setPadding(8);
        amountsTable.addCell(labelCell);

        Cell valueCell = new Cell()
                .add(new Paragraph(String.format("$ %.2f", pago.getSaldoNuevo()))
                        .setFont(fontBold).setFontSize(11))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBackgroundColor(HEADER_BG)
                .setFontColor(ColorConstants.WHITE)
                .setBorder(new SolidBorder(HEADER_BG, 1))
                .setPadding(8);
        amountsTable.addCell(valueCell);

        document.add(amountsTable);

        // Payment method
        String formaPagoText = pago.getFormaPago() != null ? pago.getFormaPago().name() : "EFECTIVO";
        String checkboxEfectivo = formaPagoText.equals("EFECTIVO") ? "[X]" : "[ ]";
        String checkboxTransf = formaPagoText.equals("TRANSFERENCIA") ? "[X]" : "[ ]";

        document.add(new Paragraph("Forma de pago: " + checkboxEfectivo + " Efectivo   " + checkboxTransf + " Transferencia")
                .setFont(fontNormal).setFontSize(9).setMarginBottom(3));

        if (pago.getReferenciaBancaria() != null && !pago.getReferenciaBancaria().isEmpty()) {
            document.add(new Paragraph("Referencia: " + pago.getReferenciaBancaria())
                    .setFont(fontNormal).setFontSize(9).setMarginBottom(10));
        } else {
            document.add(new Paragraph("Referencia: N/A")
                    .setFont(fontNormal).setFontSize(9).setMarginBottom(10));
        }

        // Separator
        document.add(new Paragraph("").setBorderBottom(new SolidBorder(BORDER_COLOR, 1)).setMarginBottom(15));

        // Signatures
        Table signaturesTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(15);

        signaturesTable.addCell(createSignatureCell("Firma del Trabajador", fontBold, fontNormal));
        signaturesTable.addCell(createSignatureCell("Firma del Responsable", fontBold, fontNormal));

        document.add(signaturesTable);

        // Footer
        document.add(new Paragraph("Fecha de emisión: " + (pago.getFecha() != null ? pago.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : ""))
                .setFont(fontNormal).setFontSize(8).setTextAlignment(TextAlignment.CENTER).setFontColor(new DeviceRgb(128, 128, 128)));

        document.close();
        return baos.toByteArray();
    }

    private Cell createAmountLabelCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }

    private Cell createAmountValueCell(String text, PdfFont font, boolean highlight) {
        Cell cell = new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER)
                .setPadding(5);

        if (highlight) {
            cell.setBackgroundColor(LIGHT_GREEN);
        }

        return cell;
    }

    private Cell createSignatureCell(String title, PdfFont fontBold, PdfFont fontNormal) {
        Cell cell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setPadding(10);

        cell.add(new Paragraph("")
                .setMarginTop(30)
                .setBorderBottom(new SolidBorder(TEXT_DARK, 0.5f)));

        cell.add(new Paragraph(title)
                .setFont(fontNormal)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(3));

        return cell;
    }
}
