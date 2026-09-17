package com.kynsoft.report.infrastructure.services;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.kynsoft.report.domain.dto.ConfiguracionEmpresaDto;
import com.kynsoft.report.domain.dto.InformeRecepcionDto;
import com.kynsoft.report.domain.services.IConfiguracionEmpresaService;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/** PDF de consulta del modelo SC-2-04; no modifica la recepción ni el stock. */
@Service
public class InformeRecepcionPdfService {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat CANTIDAD = new DecimalFormat("#,##0.####");
    private static final DecimalFormat MONEDA = new DecimalFormat("#,##0.00");
    private final InformeRecepcionService service;
    private final IConfiguracionEmpresaService empresaService;
    public InformeRecepcionPdfService(InformeRecepcionService service, IConfiguracionEmpresaService empresaService) { this.service=service; this.empresaService=empresaService; }
    public byte[] generar(UUID id) throws Exception { return generar(service.obtener(id), empresaService.findActive().orElseGet(ConfiguracionEmpresaDto::new)); }
    public byte[] generar(InformeRecepcionDto i, ConfiguracionEmpresaDto e) throws Exception {
        ByteArrayOutputStream out=new ByteArrayOutputStream(); Document d=new Document(new PdfDocument(new PdfWriter(out)),PageSize.LETTER); d.setMargins(25,26,25,26); PdfFont n=PdfFontFactory.createFont("Helvetica"), b=PdfFontFactory.createFont("Helvetica-Bold");
        Table h=new Table(UnitValue.createPercentArray(new float[]{60,40})).useAllAvailableWidth(); Cell entidad=cell(); entidad.add(line("Entidad:",e.getNombre(),n,b)); entidad.add(line("Código:",e.getCodigo(),n,b)); entidad.add(line("Dirección:",e.getDireccionCompleta(),n,b)); h.addCell(entidad);
        Cell modelo=cell().setTextAlignment(TextAlignment.CENTER); modelo.add(new Paragraph("MODELO SC-2-04").setFont(b).setFontSize(11)); modelo.add(new Paragraph("INFORME DE RECEPCIÓN").setFont(b).setFontSize(10)); modelo.add(line("Consecutivo:",i.getNumeroDocumento(),n,b).setTextAlignment(TextAlignment.LEFT)); modelo.add(line("Fecha:",i.getFechaDocumento()==null?"":FECHA.format(i.getFechaDocumento()),n,b).setTextAlignment(TextAlignment.LEFT)); h.addCell(modelo); d.add(h);
        Table datos=new Table(UnitValue.createPercentArray(new float[]{25,25,25,25})).useAllAvailableWidth().setMarginTop(8); datos.addCell(cell().add(line("Fuente:",i.getTipoFuente()==null?"":i.getTipoFuente().name(),n,b))); datos.addCell(cell().add(line("No. fuente:",i.getNumeroFuente(),n,b))); datos.addCell(cell().add(line("Proveedor/remitente:",i.getProveedor(),n,b))); datos.addCell(cell().add(line("Estado:",i.getEstado(),n,b))); d.add(datos);
        Table t=new Table(UnitValue.createPercentArray(new float[]{12,25,9,11,12,12,10,9})).useAllAvailableWidth().setMarginTop(10); for(String x:new String[]{"Código","Producto","U/M","Cantidad","Costo unit.","Importe","Saldo","Recibido"})t.addHeaderCell(cell().setBackgroundColor(new DeviceRgb(230,230,230)).add(new Paragraph(x).setFont(b).setFontSize(7).setTextAlignment(TextAlignment.CENTER)));
        double total=0; for(InformeRecepcionDto.Linea l:i.getLineas()){total+=l.getImporte()==null?0:l.getImporte();String[] x={l.getProductoCodigo(),l.getProductoNombre(),l.getUnidadMedida(),q(l.getCantidad()),m(l.getCostoUnitario()),m(l.getImporte()),q(l.getSaldoPosterior()),q(l.getCantidad())};for(int a=0;a<x.length;a++)t.addCell(cell().add(new Paragraph(s(x[a])).setFont(n).setFontSize(8).setTextAlignment(a==1?TextAlignment.LEFT:TextAlignment.CENTER)));} Cell tt=new Cell(1,5).setBorder(new SolidBorder(new DeviceRgb(100,100,100),.6f)).add(new Paragraph("TOTAL").setFont(b).setTextAlignment(TextAlignment.RIGHT));t.addCell(tt);t.addCell(cell().add(new Paragraph(m(total)).setFont(b).setTextAlignment(TextAlignment.RIGHT)));t.addCell(cell());t.addCell(cell());d.add(t);
        d.add(new Paragraph("Observaciones: "+s(i.getObservaciones())).setFont(n).setFontSize(8).setMarginTop(8)); Table firmas=new Table(UnitValue.createPercentArray(new float[]{33,34,33})).useAllAvailableWidth().setMarginTop(20);firma(firmas,"ENTREGADO POR",i.getResponsableEntrega(),n,b);firma(firmas,"RECIBIDO POR",i.getResponsableRecibe(),n,b);firma(firmas,"CONTABILIDAD","",n,b);d.add(firmas);d.add(new Paragraph("Modelo SC-2-04 — Informe de Recepción. Resolución No. 11/2007 del MFP.").setFont(n).setFontSize(7).setTextAlignment(TextAlignment.CENTER).setMarginTop(10));d.close();return out.toByteArray();
    }
    private Cell cell(){return new Cell().setBorder(new SolidBorder(new DeviceRgb(100,100,100),.6f)).setPadding(4);} private Paragraph line(String a,String v,PdfFont n,PdfFont b){return new Paragraph().setMargin(0).setFontSize(8).add(new Text(a+" ").setFont(b)).add(new Text(s(v)).setFont(n));} private void firma(Table t,String r,String nombre,PdfFont n,PdfFont b){Cell c=cell().setMinHeight(64);c.add(new Paragraph(r).setFont(b).setFontSize(7).setTextAlignment(TextAlignment.CENTER));c.add(new Paragraph("\n________________________").setFont(n).setTextAlignment(TextAlignment.CENTER));c.add(new Paragraph(s(nombre)).setFont(n).setFontSize(7).setTextAlignment(TextAlignment.CENTER));t.addCell(c);} private String s(String v){return v==null||v.isBlank()?"—":v;} private String q(Double v){return v==null?"—":CANTIDAD.format(v);} private String m(Double v){return v==null?"—":MONEDA.format(v);}
}
