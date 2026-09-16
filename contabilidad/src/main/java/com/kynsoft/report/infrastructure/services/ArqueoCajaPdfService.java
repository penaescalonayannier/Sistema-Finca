package com.kynsoft.report.infrastructure.services;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.kynsoft.report.domain.dto.ArqueoCajaDenominacionDto;
import com.kynsoft.report.domain.dto.ArqueoCajaDetalleDto;
import com.kynsoft.report.domain.services.IArqueoCajaService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class ArqueoCajaPdfService {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final IArqueoCajaService arqueoCajaService;

    public ArqueoCajaPdfService(IArqueoCajaService arqueoCajaService) {
        this.arqueoCajaService = arqueoCajaService;
    }

    public byte[] generar(java.util.UUID id) {
        ArqueoCajaDetalleDto arqueo = arqueoCajaService.detalle(id);
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document documento = new Document(new PdfDocument(new PdfWriter(salida)), PageSize.A4);
            PdfFont normal = PdfFontFactory.createFont("Helvetica");
            PdfFont negrita = PdfFontFactory.createFont("Helvetica-Bold");
            documento.setMargins(32, 32, 32, 32);
            documento.add(new Paragraph("ACTA DE ARQUEO SORPRESIVO DE CAJA").setFont(negrita).setFontSize(15)
                    .setTextAlignment(TextAlignment.CENTER));
            documento.add(new Paragraph("No. " + arqueo.getNumero() + "  |  Alcance: efectivo CUP por denominaciones"
                    + "  |  Tipo: " + arqueo.getTipo()).setFont(normal).setFontSize(9).setTextAlignment(TextAlignment.CENTER));
            documento.add(new Paragraph("Este arqueo es un documento de control y NO ajusta el saldo ni los movimientos de caja.")
                    .setFont(negrita).setFontSize(9).setTextAlignment(TextAlignment.CENTER));
            documento.add(informacion(arqueo, normal, negrita));
            documento.add(new Paragraph("Detalle del conteo").setFont(negrita).setFontSize(11).setMarginTop(12));
            documento.add(tabla(arqueo, normal, negrita));
            documento.add(resumen(arqueo, normal, negrita));
            documento.add(new Paragraph("Observaciones de apertura: " + texto(arqueo.getObservacionesApertura())).setFont(normal).setFontSize(9).setMarginTop(10));
            documento.add(new Paragraph("Observaciones de cierre: " + texto(arqueo.getObservacionesCierre())).setFont(normal).setFontSize(9));
            documento.add(firmas(arqueo, normal, negrita));
            documento.close();
            return salida.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el acta PDF del arqueo de caja.", ex);
        }
    }

    private Table informacion(ArqueoCajaDetalleDto arqueo, PdfFont normal, PdfFont bold) {
        Table tabla = new Table(new float[]{1, 1}).useAllAvailableWidth().setMarginTop(12);
        fila(tabla, "Finca", texto(arqueo.getFincaCodigo()) + " - " + texto(arqueo.getFincaNombre()), bold, normal);
        fila(tabla, "Estado", texto(arqueo.getEstado()), bold, normal);
        fila(tabla, "Apertura", fecha(arqueo.getFechaApertura()), bold, normal);
        fila(tabla, "Cierre", fecha(arqueo.getFechaCierre()), bold, normal);
        fila(tabla, "Contador responsable", texto(arqueo.getContadorResponsable()), bold, normal);
        fila(tabla, "Custodio", texto(arqueo.getCustodio()), bold, normal);
        fila(tabla, "Recibido por", texto(arqueo.getRecibidoPor()), bold, normal);
        fila(tabla, "Denominaciones revisadas", String.valueOf(arqueo.getDenominaciones().size()), bold, normal);
        return tabla;
    }

    private Table tabla(ArqueoCajaDetalleDto arqueo, PdfFont normal, PdfFont bold) {
        Table tabla = new Table(new float[]{1.2f, 1.4f, 1.4f, 1.4f, 1.8f}).useAllAvailableWidth();
        for (String titulo : new String[]{"Billete CUP", "Esperado", "Físico", "Diferencia", "Diferencia CUP"}) {
            tabla.addHeaderCell(celda(titulo, bold, TextAlignment.CENTER));
        }
        for (ArqueoCajaDenominacionDto fila : arqueo.getDenominaciones()) {
            tabla.addCell(celda(formato(fila.getDenominacion()), normal, TextAlignment.RIGHT));
            tabla.addCell(celda(formato(fila.getCantidadEsperada()), normal, TextAlignment.RIGHT));
            tabla.addCell(celda(fila.getCantidadFisica() == null ? "Pendiente" : formato(fila.getCantidadFisica()), normal, TextAlignment.RIGHT));
            tabla.addCell(celda(fila.getDiferenciaCantidad() == null ? "-" : formato(fila.getDiferenciaCantidad()), normal, TextAlignment.RIGHT));
            tabla.addCell(celda(fila.getDiferenciaImporte() == null ? "-" : formatoMoneda(fila.getDiferenciaImporte()), normal, TextAlignment.RIGHT));
        }
        return tabla;
    }

    private Table resumen(ArqueoCajaDetalleDto arqueo, PdfFont normal, PdfFont bold) {
        Table tabla = new Table(new float[]{2, 1}).setWidth(260).setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT).setMarginTop(10);
        fila(tabla, "Total esperado", formatoMoneda(arqueo.getTotalEsperado()), bold, normal);
        fila(tabla, "Total físico", arqueo.getTotalFisico() == null ? "Pendiente" : formatoMoneda(arqueo.getTotalFisico()), bold, normal);
        fila(tabla, "Diferencia", arqueo.getDiferencia() == null ? "Pendiente" : formatoMoneda(arqueo.getDiferencia()), bold, normal);
        return tabla;
    }

    private Table firmas(ArqueoCajaDetalleDto arqueo, PdfFont normal, PdfFont bold) {
        Table tabla = new Table(new float[]{1, 1, 1}).useAllAvailableWidth().setMarginTop(45);
        firma(tabla, "Contador responsable", arqueo.getContadorResponsable(), normal, bold);
        firma(tabla, "Custodio", arqueo.getCustodio(), normal, bold);
        firma(tabla, "Recibido por", arqueo.getRecibidoPor(), normal, bold);
        return tabla;
    }

    private void firma(Table tabla, String rol, String nombre, PdfFont normal, PdfFont bold) {
        tabla.addCell(new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
                .add(new Paragraph("____________________________").setFont(normal))
                .add(new Paragraph(rol).setFont(bold).setFontSize(8))
                .add(new Paragraph(texto(nombre)).setFont(normal).setFontSize(8)));
    }

    private void fila(Table tabla, String etiqueta, String valor, PdfFont bold, PdfFont normal) {
        tabla.addCell(celda(etiqueta, bold, TextAlignment.LEFT));
        tabla.addCell(celda(valor, normal, TextAlignment.LEFT));
    }
    private Cell celda(String texto, PdfFont fuente, TextAlignment alineacion) { return new Cell().add(new Paragraph(texto).setFont(fuente).setFontSize(8)).setTextAlignment(alineacion); }
    private String fecha(java.time.LocalDateTime fecha) { return fecha == null ? "Pendiente" : FECHA.format(fecha); }
    private String texto(Object valor) { return valor == null || valor.toString().isBlank() ? "-" : valor.toString(); }
    private String formato(Number valor) { return valor == null ? "-" : String.format("%,d", valor.longValue()); }
    private String formatoMoneda(Double valor) { return valor == null ? "-" : String.format("%,.2f CUP", valor); }
}
