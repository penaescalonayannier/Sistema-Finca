package com.kynsoft.report.infrastructure.services;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.kynsoft.report.domain.dto.ExpedienteDisciplinarioDto;
import com.kynsoft.report.domain.services.IExpedienteDisciplinarioService;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

/** PDF de consulta: no reemplaza una notificación firmada ni genera efectos de nómina. */
@Service
public class ExpedienteDisciplinarioPdfService {
    private final IExpedienteDisciplinarioService service;
    public ExpedienteDisciplinarioPdfService(IExpedienteDisciplinarioService service) { this.service = service; }
    public byte[] generar(java.util.UUID id) {
        ExpedienteDisciplinarioDto e = service.detalle(id);
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document d = new Document(new PdfDocument(new PdfWriter(salida)));
            var normal = PdfFontFactory.createFont("Helvetica"); var bold = PdfFontFactory.createFont("Helvetica-Bold");
            d.setMargins(34, 38, 34, 38);
            d.add(new Paragraph("EXPEDIENTE DE DISCIPLINA LABORAL").setFont(bold).setFontSize(15).setTextAlignment(TextAlignment.CENTER));
            d.add(new Paragraph("Documento interno de control. Estado: " + e.getEstado()).setFont(normal).setFontSize(9).setTextAlignment(TextAlignment.CENTER));
            Table t = new Table(new float[]{1, 2}).useAllAvailableWidth().setMarginTop(15);
            fila(t, "Finca", valor(e.getFincaNombre()), bold, normal); fila(t, "Trabajador", valor(e.getTrabajadorNombre()), bold, normal);
            fila(t, "Fecha del hecho", e.getFecha() == null ? "-" : e.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), bold, normal);
            fila(t, "Tipo de incidencia", valor(e.getTipo()), bold, normal); fila(t, "Aprobador", valor(e.getAprobadorNombre()), bold, normal);
            fila(t, "Fecha de notificación", fechaHora(e.getFechaNotificacion()), bold, normal); fila(t, "Fecha de resolución", fechaHora(e.getFechaResolucion()), bold, normal);
            d.add(t); bloque(d, "Descripción del hecho", e.getDescripcion(), bold, normal); bloque(d, "Evidencia / referencia", e.getEvidencia(), bold, normal);
            bloque(d, "Medida", e.getMedida(), bold, normal); bloque(d, "Resolución", e.getResolucion(), bold, normal); bloque(d, "Observaciones", e.getObservaciones(), bold, normal);
            if (e.getFechaAnulacion() != null) { bloque(d, "ANULADO", "Motivo: " + valor(e.getMotivoAnulacion()) + "\nFecha: " + fechaHora(e.getFechaAnulacion()), bold, normal); }
            Table firmas = new Table(new float[]{1, 1, 1}).useAllAvailableWidth().setMarginTop(45);
            for (String cargo : new String[]{"Trabajador notificado", "Aprobador", "Responsable de RR. HH."}) firmas.addCell(new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).add(new Paragraph("________________________").setFont(normal)).add(new Paragraph(cargo).setFont(bold).setFontSize(8)));
            d.add(firmas); d.close(); return salida.toByteArray();
        } catch (Exception ex) { throw new IllegalStateException("No se pudo generar el PDF del expediente disciplinario.", ex); }
    }
    private void fila(Table t, String etiqueta, String valor, com.itextpdf.kernel.font.PdfFont bold, com.itextpdf.kernel.font.PdfFont normal) { t.addCell(new Cell().add(new Paragraph(etiqueta).setFont(bold).setFontSize(9))); t.addCell(new Cell().add(new Paragraph(valor).setFont(normal).setFontSize(9))); }
    private void bloque(Document d, String titulo, String contenido, com.itextpdf.kernel.font.PdfFont bold, com.itextpdf.kernel.font.PdfFont normal) { d.add(new Paragraph(titulo).setFont(bold).setFontSize(10).setMarginTop(12)); d.add(new Paragraph(valor(contenido)).setFont(normal).setFontSize(9).setMarginTop(0)); }
    private String valor(Object value) { return value == null || value.toString().isBlank() ? "-" : value.toString(); }
    private String fechaHora(java.time.LocalDateTime value) { return value == null ? "-" : value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")); }
}
