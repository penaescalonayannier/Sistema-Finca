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
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.kynsoft.report.domain.dto.ConfiguracionEmpresaDto;
import com.kynsoft.report.domain.dto.KardexDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.infrastructure.entity.Almacen;
import com.kynsoft.report.infrastructure.entity.FincaProducto;
import com.kynsoft.report.infrastructure.entity.MovimientoStock;
import com.kynsoft.report.infrastructure.repository.query.AlmacenReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.FincaProductoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.MovimientoStockReadDataJPARepository;
import com.kynsoft.report.domain.services.IConfiguracionEmpresaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Tarjeta de estiba SC-2-14 consolidada para un producto de una finca.
 *
 * <p>A diferencia de la tarjeta por almacén, esta vista suma los movimientos
 * de todos los almacenes de la finca. Las transferencias internas aparecen en
 * sus dos filas y se compensan en el saldo total; cada fila conserva el
 * almacén que originó el movimiento. Es un documento de consulta y no altera
 * existencias ni contabilidad.</p>
 */
@Service
public class TarjetaEstibaFincaPdfService {

    private static final DeviceRgb HEADER = new DeviceRgb(220, 230, 240);
    private static final DeviceRgb BORDER = new DeviceRgb(90, 90, 90);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DecimalFormat QUANTITY = new DecimalFormat("#,##0.####",
            DecimalFormatSymbols.getInstance(Locale.US));

    private final MovimientoStockReadDataJPARepository movimientoRepository;
    private final FincaProductoReadDataJPARepository fincaProductoRepository;
    private final AlmacenReadDataJPARepository almacenRepository;
    private final IConfiguracionEmpresaService configuracionEmpresaService;

    public TarjetaEstibaFincaPdfService(MovimientoStockReadDataJPARepository movimientoRepository,
                                        FincaProductoReadDataJPARepository fincaProductoRepository,
                                        AlmacenReadDataJPARepository almacenRepository,
                                        IConfiguracionEmpresaService configuracionEmpresaService) {
        this.movimientoRepository = movimientoRepository;
        this.fincaProductoRepository = fincaProductoRepository;
        this.almacenRepository = almacenRepository;
        this.configuracionEmpresaService = configuracionEmpresaService;
    }

    @Transactional(readOnly = true, transactionManager = "readTransactionManager")
    public byte[] generar(UUID fincaProductoId, LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        validarPeriodo(fechaInicio, fechaFin);
        FincaProducto fincaProducto = fincaProductoRepository.findByIdWithDetails(fincaProductoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto de finca no encontrado."));
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        List<MovimientoStock> anteriores = movimientoRepository
                .findByFincaProductoIdAndFechaBefore(fincaProductoId, inicio);
        List<MovimientoStock> delPeriodo = movimientoRepository
                .findByFincaProductoIdAndFechaBetween(fincaProductoId, inicio, fin);

        Map<UUID, String> almacenes = nombresAlmacenes(unir(anteriores, delPeriodo));
        KardexDto kardex = construirKardex(fincaProducto, anteriores, delPeriodo, almacenes);
        ConfiguracionEmpresaDto empresa = configuracionEmpresaService.findActive()
                .orElseGet(ConfiguracionEmpresaDto::new);
        DatosFincaProducto datos = new DatosFincaProducto(
                fincaProducto.getFinca().getCode(), fincaProducto.getFinca().getName(),
                fincaProducto.getProducto().getCode(), fincaProducto.getProducto().getName(),
                fincaProducto.getProducto().getUnidadMedida() == null ? "" : fincaProducto.getProducto().getUnidadMedida().name());
        return generar(kardex, datos, empresa, fechaInicio, fechaFin);
    }

    /** Variante sin acceso a datos, para pruebas unitarias. */
    public byte[] generar(KardexDto kardex, DatosFincaProducto datos, ConfiguracionEmpresaDto empresa,
                          LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        validarPeriodo(fechaInicio, fechaFin);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(new PdfDocument(new PdfWriter(output)), PageSize.A4.rotate());
        document.setMargins(20, 20, 24, 20);
        PdfFont normal = PdfFontFactory.createFont("Helvetica");
        PdfFont bold = PdfFontFactory.createFont("Helvetica-Bold");

        document.add(cabecera(datos, empresa, fechaInicio, fechaFin, normal, bold));
        document.add(movimientos(kardex, normal, bold));
        document.add(resumen(kardex, normal, bold));
        document.add(firmas(normal, bold));
        document.add(new Paragraph("Modelo SC-2-14 — Tarjeta de Estiba consolidada por finca. "
                + "Resolución No. 11/2007 del MFP. Documento de consulta; no modifica inventario ni contabilidad.")
                .setFont(normal).setFontSize(7).setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER).setMarginTop(9));
        document.close();
        return output.toByteArray();
    }

    private KardexDto construirKardex(FincaProducto fincaProducto, List<MovimientoStock> anteriores,
                                      List<MovimientoStock> delPeriodo, Map<UUID, String> almacenes) {
        double saldo = anteriores.stream().mapToDouble(this::variacion).sum();
        double saldoInicial = saldo;
        double entradas = 0d;
        double salidas = 0d;
        List<KardexDto.MovimientoKardexDto> filas = new java.util.ArrayList<>();
        // Construcción imperativa: conserva orden cronológico y saldo luego de cada movimiento.
        List<MovimientoStock> ordenados = delPeriodo.stream()
                .sorted(Comparator.comparing(MovimientoStock::getFecha, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(MovimientoStock::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        for (MovimientoStock movimiento : ordenados) {
            double variacion = variacion(movimiento);
            double cantidad = cantidad(movimiento);
            if (variacion > 0) entradas += cantidad;
            if (variacion < 0) salidas += cantidad;
            saldo += variacion;
            filas.add(KardexDto.MovimientoKardexDto.builder()
                    .fecha(movimiento.getFecha()).tipoMovimiento(movimiento.getTipo())
                    .entrada(variacion > 0 ? cantidad : 0d).salida(variacion < 0 ? cantidad : 0d)
                    .saldo(saldo).referenciaId(movimiento.getReferenciaId())
                    .referenciaTabla(movimiento.getReferenciaTabla()).descripcion(movimiento.getDescripcion())
                    .observaciones(movimiento.getObservaciones()).almacenId(movimiento.getAlmacenId())
                    .almacenNombre(almacenes.getOrDefault(movimiento.getAlmacenId(), "Sin almacén asociado"))
                    .build());
        }
        return KardexDto.builder().producto(KardexDto.ProductoInfoDto.builder()
                        .fincaProductoId(fincaProducto.getId()).productoId(fincaProducto.getProducto().getId())
                        .productoCode(fincaProducto.getProducto().getCode()).productoName(fincaProducto.getProducto().getName()).build())
                .stockInicial(saldoInicial).movimientos(filas).totalEntradas(entradas)
                .totalSalidas(salidas).stockFinal(saldo).build();
    }

    private Table cabecera(DatosFincaProducto datos, ConfiguracionEmpresaDto empresa, LocalDate inicio,
                           LocalDate fin, PdfFont normal, PdfFont bold) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{42, 33, 25})).useAllAvailableWidth().setMarginBottom(9);
        Cell entidad = celda();
        entidad.add(linea("Entidad:", safe(empresa.getNombre()), normal, bold));
        entidad.add(linea("Código:", safe(empresa.getCodigo()), normal, bold));
        entidad.add(linea("Finca:", safe(datos.fincaCodigo()) + " " + safe(datos.fincaNombre()), normal, bold));
        entidad.add(linea("Almacenes:", "Todos los almacenes de la finca", normal, bold));
        table.addCell(entidad);
        Cell producto = celda();
        producto.add(linea("Código producto:", safe(datos.productoCodigo()), normal, bold));
        producto.add(linea("Producto:", safe(datos.productoNombre()), normal, bold));
        producto.add(linea("Unidad de medida:", safe(datos.unidadMedida()), normal, bold));
        producto.add(linea("Período:", DATE.format(inicio) + " al " + DATE.format(fin), normal, bold));
        table.addCell(producto);
        table.addCell(celda().setTextAlignment(TextAlignment.CENTER)
                .add(new Paragraph("MODELO SC-2-14").setFont(bold).setFontSize(11).setMarginBottom(4))
                .add(new Paragraph("TARJETA DE ESTIBA").setFont(bold).setFontSize(13).setMarginBottom(8))
                .add(new Paragraph("Control consolidado por finca").setFont(normal).setFontSize(8)));
        return table;
    }

    private Table movimientos(KardexDto kardex, PdfFont normal, PdfFont bold) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{10, 16, 13, 14, 20, 9, 9, 9}))
                .useAllAvailableWidth();
        String[] headers = {"Fecha", "Almacén originador", "Tipo", "Documento / No.", "Concepto", "Entrada", "Salida", "Existencia"};
        for (String header : headers) table.addHeaderCell(celda().setBackgroundColor(HEADER).setPadding(4)
                .setTextAlignment(TextAlignment.CENTER).add(new Paragraph(header).setFont(bold).setFontSize(7)));
        List<KardexDto.MovimientoKardexDto> filas = kardex.getMovimientos() == null ? List.of() : kardex.getMovimientos();
        for (KardexDto.MovimientoKardexDto fila : filas) {
            agregar(table, fila.getFecha() == null ? "" : DATE_TIME.format(fila.getFecha()), normal, TextAlignment.CENTER);
            agregar(table, safe(fila.getAlmacenNombre()), normal, TextAlignment.LEFT);
            agregar(table, fila.getTipoMovimiento() == null ? "" : fila.getTipoMovimiento().name().replace('_', ' '), normal, TextAlignment.LEFT);
            agregar(table, referencia(fila), normal, TextAlignment.LEFT);
            agregar(table, !safe(fila.getDescripcion()).isBlank() ? fila.getDescripcion() : safe(fila.getObservaciones()), normal, TextAlignment.LEFT);
            agregar(table, cantidad(fila.getEntrada()), normal, TextAlignment.RIGHT);
            agregar(table, cantidad(fila.getSalida()), normal, TextAlignment.RIGHT);
            agregar(table, cantidad(fila.getSaldo()), normal, TextAlignment.RIGHT);
        }
        if (filas.isEmpty()) table.addCell(new Cell(1, 8).setBorder(new SolidBorder(BORDER, .5f)).setPadding(5)
                .add(new Paragraph("No se registran movimientos del producto en el período seleccionado.")
                        .setFont(normal).setFontSize(8).setTextAlignment(TextAlignment.CENTER)));
        return table;
    }

    private Table resumen(KardexDto kardex, PdfFont normal, PdfFont bold) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1})).useAllAvailableWidth().setMarginTop(9);
        resumen(table, "Saldo inicial", kardex.getStockInicial(), normal, bold);
        resumen(table, "Total entradas", kardex.getTotalEntradas(), normal, bold);
        resumen(table, "Total salidas", kardex.getTotalSalidas(), normal, bold);
        resumen(table, "Saldo final", kardex.getStockFinal(), normal, bold);
        return table;
    }

    private void resumen(Table table, String etiqueta, Double valor, PdfFont normal, PdfFont bold) {
        table.addCell(celda().setBackgroundColor(HEADER).setTextAlignment(TextAlignment.CENTER)
                .add(new Paragraph(etiqueta).setFont(bold).setFontSize(8))
                .add(new Paragraph(cantidad(valor)).setFont(normal).setFontSize(11)));
    }

    private Table firmas(PdfFont normal, PdfFont bold) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1})).useAllAvailableWidth().setMarginTop(9);
        for (String cargo : List.of("Responsable de almacén", "Control de inventario", "Contabilidad")) table.addCell(new Cell()
                .setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setPaddingTop(20)
                .add(new Paragraph("_______________________________").setFont(normal).setFontSize(9))
                .add(new Paragraph(cargo).setFont(bold).setFontSize(8))
                .add(new Paragraph("Nombre, firma y fecha").setFont(normal).setFontSize(7)));
        return table;
    }

    private Map<UUID, String> nombresAlmacenes(Collection<MovimientoStock> movimientos) {
        List<UUID> ids = movimientos.stream().map(MovimientoStock::getAlmacenId).filter(java.util.Objects::nonNull).distinct().toList();
        Map<UUID, String> resultado = new HashMap<>();
        for (Almacen almacen : almacenRepository.findAllById(ids)) resultado.put(almacen.getId(),
                safe(almacen.getNombre()) + (safe(almacen.getInventario()).isBlank() ? "" : " (" + almacen.getInventario() + ")"));
        return resultado;
    }

    private List<MovimientoStock> unir(List<MovimientoStock> anteriores, List<MovimientoStock> periodo) {
        List<MovimientoStock> todos = new java.util.ArrayList<>(anteriores);
        todos.addAll(periodo);
        return todos;
    }

    private double variacion(MovimientoStock movimiento) {
        double cantidad = cantidad(movimiento);
        TipoMovimientoStock tipo = movimiento.getTipo();
        if (tipo != null && tipo.isEntrada()) return cantidad;
        if (tipo != null && tipo.isSalida()) return -cantidad;
        // Registros antiguos de ajustes sin un tipo direccional conservan el signo original.
        return movimiento.getCantidad() == null ? 0d : movimiento.getCantidad();
    }

    private double cantidad(MovimientoStock movimiento) { return movimiento.getCantidad() == null ? 0d : Math.abs(movimiento.getCantidad()); }
    private void validarPeriodo(LocalDate inicio, LocalDate fin) { if (inicio == null || fin == null || fin.isBefore(inicio)) throw new IllegalArgumentException("El período seleccionado para la tarjeta de estiba es inválido."); }
    private Cell celda() { return new Cell().setBorder(new SolidBorder(BORDER, .5f)).setPadding(4); }
    private void agregar(Table table, String texto, PdfFont font, TextAlignment alignment) { table.addCell(celda().setTextAlignment(alignment).add(new Paragraph(texto).setFont(font).setFontSize(7))); }
    private Paragraph linea(String etiqueta, String valor, PdfFont normal, PdfFont bold) { return new Paragraph().setFontSize(8).setMargin(0).add(new Text(etiqueta + " ").setFont(bold)).add(new Text(valor).setFont(normal)); }
    private String cantidad(Double valor) { return valor == null || Math.abs(valor) < .0000001d ? "" : QUANTITY.format(valor); }
    private String safe(String valor) { return valor == null ? "" : valor; }
    private String referencia(KardexDto.MovimientoKardexDto movimiento) { String tabla = safe(movimiento.getReferenciaTabla()).replace('_', ' '); String id = movimiento.getReferenciaId() == null ? "" : movimiento.getReferenciaId().toString().substring(0, 8).toUpperCase(Locale.ROOT); return tabla.isBlank() ? id : tabla + (id.isBlank() ? "" : " #" + id); }

    public record DatosFincaProducto(String fincaCodigo, String fincaNombre, String productoCodigo,
                                     String productoNombre, String unidadMedida) { }

}
