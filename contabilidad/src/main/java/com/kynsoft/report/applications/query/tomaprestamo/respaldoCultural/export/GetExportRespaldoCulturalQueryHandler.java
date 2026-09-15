package com.kynsoft.report.applications.query.tomaprestamo.respaldoCultural.export;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.report.estadoCuenta.export.Response;
import com.kynsoft.report.domain.dto.PrestamoDto;
import com.kynsoft.report.domain.dto.TomaPrestamoDto;
import com.kynsoft.report.domain.dto.enumerativos.TipoTomaPrestamo;
import com.kynsoft.report.domain.services.IPrestamoService;
import com.kynsoft.report.domain.services.ITomaPrestamoService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.UUID;

@Component
public class GetExportRespaldoCulturalQueryHandler implements IQueryHandler<GetExportRespaldoCulturalQuery, Response> {

    // --- Constantes de columnas (0-indexed) ---
    private static final int COL_A = 0;  // Fila
    private static final int COL_B = 1;  // Concepto
    private static final int COL_C = 2;  // Tn de Caña
    private static final int COL_D = 3;  // Aprobado
    private static final int COL_E = 4;  // Efectivo (Aprobado)
    private static final int COL_F = 5;  // No Efectivo (Aprobado)
    private static final int COL_G = 6;  // Efectivo (Utilizado)
    private static final int COL_H = 7;  // No Efectivo (Utilizado)
    private static final int COL_I = 8;  // Efectivo (Disponible)
    private static final int COL_J = 9;  // No Efectivo (Disponible)

    // --- Constantes de filas (0-indexed) basadas en la plantilla ---
    private static final int ROW_TITLE = 0;
    private static final int ROW_EMPRESA = 1;
    private static final int ROW_APA = 2;
    private static final int ROW_UPC = 3;
    private static final int ROW_CTA_OPERACION = 4;
    private static final int ROW_SUCURSAL = 5;
    private static final int ROW_HEADERS_CREDITOS = 6;
    private static final int ROW_HEADERS_TABLE = 7;
    private static final int ROW_APROBADO_AC = 8;
    private static final int ROW_DIST_NO_EFECTIVO_TITLE = 9;
    private static final int ROW_SUBTOTAL_NO_EFECTIVO = 13;
    private static final int ROW_DIST_EFECTIVO_TITLE = 15;
    private static final int ROW_SUBTOTAL_EFECTIVO = 17;
    private static final int ROW_TOTAL = 18;
    private static final int ROW_APA_SECTION = 21;
    private static final int ROW_UPC_SECTION = 24;
    private static final int ROW_CONSECUTIVO = 27;

    private final ITomaPrestamoService tomaPrestamoService;
    private final IPrestamoService prestamoService;

    public GetExportRespaldoCulturalQueryHandler(ITomaPrestamoService tomaPrestamoService,
            IPrestamoService prestamoService) {
        this.tomaPrestamoService = tomaPrestamoService;
        this.prestamoService = prestamoService;
    }

    @Override
    public Response handle(GetExportRespaldoCulturalQuery query) {
        TomaPrestamoDto tomaPrestamo = tomaPrestamoService.findById(query.getTomaPrestamoId());
        PrestamoDto prestamo = prestamoService.findById(UUID.fromString(tomaPrestamo.getCreditoId()));
        return Response.builder().outputStream(this.response(tomaPrestamo, prestamo)).build();
    }

    private StreamingResponseBody response(TomaPrestamoDto tomaPrestamo, PrestamoDto prestamo) {
        return outputStream -> {
            // 1. Cargar plantilla desde classpath
            ClassPathResource resource = new ClassPathResource("Apertura.xls");
            try (InputStream templateStream = resource.getInputStream(); Workbook workbook = new HSSFWorkbook(templateStream)) {

                // 2. Obtener la hoja (la plantilla solo tiene una)
                Sheet sheet = workbook.getSheetAt(0);

                // 3. Llenar datos dinámicos
                fillTemplate(sheet, tomaPrestamo, prestamo);

                // 4. Escribir al output stream
                workbook.write(outputStream);
            } catch (Exception e) {
                throw new RuntimeException("Error al generar archivo Excel desde plantilla", e);
            }
        };
    }

    private void fillTemplate(Sheet sheet, TomaPrestamoDto tomaPrestamo, PrestamoDto prestamo) {
        // --- Información de la empresa ---
        setCellValue(sheet, ROW_EMPRESA, COL_D, "AZUCARERA LAS TUNAS");

        // --- Información del APA ---
        setCellValue(sheet, ROW_APA, COL_D, "ARGELIA LIBRE");

        // --- Información de la UPC ---
        setCellValue(sheet, ROW_UPC, COL_D, "LORENSO DALIZ");

        // --- Número de cuenta ---
        String numeroCuenta = tomaPrestamo.getCuentaDestino() != null ? tomaPrestamo.getCuentaDestino() : "O662421081590018";
        setCellValue(sheet, ROW_CTA_OPERACION, COL_G, numeroCuenta);

        // --- Sucursal bancaria ---
        setCellValue(sheet, ROW_SUCURSAL, COL_C, "6241");

        // --- Cálculos principales ---
        double aprobado = prestamo.getImporteAprobado() != null ? prestamo.getImporteAprobado() : 0.0;
        double efectivoAprobado = prestamo.getImporteAprobadoEfectivo() != null ? prestamo.getImporteAprobadoEfectivo() : 0.0;
        double noEfectivoAprobado = (prestamo.getImporteAprobadoSuministros() != null ? prestamo.getImporteAprobadoSuministros() : 0.0)
                + (prestamo.getImporteAprobadoSeguro() != null ? prestamo.getImporteAprobadoSeguro() : 0.0);
        double efectivoUtilizado = tomaPrestamo.getImporteUtilizadoEfectivo() != null ? tomaPrestamo.getImporteUtilizadoEfectivo() : 0.0;
        double noEfectivoUtilizado = (tomaPrestamo.getImporteUtilizadoSuministros() != null ? tomaPrestamo.getImporteUtilizadoSuministros() : 0.0)
                + (tomaPrestamo.getImporteUtilizadoSeguro() != null ? tomaPrestamo.getImporteUtilizadoSeguro() : 0.0);
        double efectivoDisponible = efectivoAprobado - efectivoUtilizado;
        double noEfectivoDisponible = noEfectivoAprobado - noEfectivoUtilizado;
        String toneladasCana = prestamo.getToneladasMolibles() != null ? prestamo.getToneladasMolibles().toString() : ""; // Este valor debería venir de algún lugar

        // --- Fila 8: Aprobado Atenciones Culturales ---
        Row row8 = getOrCreateRow(sheet, ROW_APROBADO_AC);
        setCellValue(row8, COL_A, "1");
        setCellValue(row8, COL_B, "Aprobado Atenciones Culturales");
        setCellValue(row8, COL_C, toneladasCana);
        setCellValue(row8, COL_D, aprobado);
        setCellValue(row8, COL_E, efectivoAprobado);
        setCellValue(row8, COL_F, noEfectivoAprobado);
        setCellValue(row8, COL_G, efectivoUtilizado);
        setCellValue(row8, COL_H, noEfectivoUtilizado);
        setCellValue(row8, COL_I, efectivoDisponible);
        setCellValue(row8, COL_J, noEfectivoDisponible);

        // --- Distribución según tipo de toma ---
        double subtotalEfectivo = 0.0;
        double subtotalNoEfectivo = 0.0;

        if (tomaPrestamo.getTipo() == TipoTomaPrestamo.EFECTIVO) {
            // Llenar distribución de efectivo
            Row row15 = getOrCreateRow(sheet, ROW_DIST_EFECTIVO_TITLE);
            String observaciones = tomaPrestamo.getObservaciones() != null
                    ? tomaPrestamo.getObservaciones() : "Anticipo";
            Double importe = tomaPrestamo.getImporte() != null ? tomaPrestamo.getImporte() : 0.0;
            subtotalEfectivo = importe;

            setCellValue(row15, COL_B, observaciones);
            setCellValue(row15, COL_G, importe);
        } else if (tomaPrestamo.getTipo() == TipoTomaPrestamo.SUMINISTRO) {
            // Llenar distribución de no efectivo
            Row row10 = getOrCreateRow(sheet, ROW_DIST_NO_EFECTIVO_TITLE + 1);
            String observaciones = tomaPrestamo.getObservaciones() != null
                    ? tomaPrestamo.getObservaciones() : "Suministros";
            Double importe = tomaPrestamo.getImporte() != null ? tomaPrestamo.getImporte() : 0.0;
            subtotalNoEfectivo = importe;

            setCellValue(row10, COL_B, observaciones);
            setCellValue(row10, COL_H, importe);
        }

        // --- Sub-totales ---
        setCellValue(sheet, ROW_SUBTOTAL_EFECTIVO, COL_G, subtotalEfectivo);
        setCellValue(sheet, ROW_SUBTOTAL_NO_EFECTIVO, COL_H, subtotalNoEfectivo);

        // --- Totales ---
        Row totalRow = getOrCreateRow(sheet, ROW_TOTAL);
        setCellValue(totalRow, COL_G, efectivoUtilizado + subtotalEfectivo);
        setCellValue(totalRow, COL_H, noEfectivoUtilizado + subtotalNoEfectivo);
        setCellValue(totalRow, COL_I, efectivoDisponible - subtotalEfectivo);
        setCellValue(totalRow, COL_J, noEfectivoDisponible - subtotalNoEfectivo);

        // --- Firmas y fechas ---
        if (tomaPrestamo.getFecha() != null) {
            // Firma APA (fila 23)
            Row firmaApaRow = getOrCreateRow(sheet, 21);
            setCellValue(firmaApaRow, COL_H, tomaPrestamo.getFecha().getDayOfMonth());
            setCellValue(firmaApaRow, COL_I, tomaPrestamo.getFecha().getMonthValue());
            setCellValue(firmaApaRow, COL_J, tomaPrestamo.getFecha().getYear());

            // Firma UPC (fila 26)
            Row firmaUpcRow = getOrCreateRow(sheet, 24);
            setCellValue(firmaUpcRow, COL_H, tomaPrestamo.getFecha().getDayOfMonth());
            setCellValue(firmaUpcRow, COL_C, "YANNIER PEÑA ESCALONA");
            setCellValue(firmaUpcRow, COL_I, tomaPrestamo.getFecha().getMonthValue());
            setCellValue(firmaUpcRow, COL_J, tomaPrestamo.getFecha().getYear());
        }

        // --- Consecutivo ---
        //setCellValue(sheet, ROW_CONSECUTIVO, COL_J, "01"); // Esto debería ser dinámico

        // Ajustar anchos de columna
        for (int i = 0; i <= 9; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // --- Métodos auxiliares ---
    private void setCellValue(Sheet sheet, int rowNum, int colNum, Object value) {
        Row row = getOrCreateRow(sheet, rowNum);
        setCellValue(row, colNum, value);
    }

    private Row getOrCreateRow(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        return row;
    }

    private void setCellValue(Row row, int colNum, Object value) {
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value != null) {
            cell.setCellValue(value.toString());
        }

        // Aplicar formato de número para valores monetarios
        if (value instanceof Double || value instanceof Integer) {
            CellStyle style = row.getSheet().getWorkbook().createCellStyle();
            style.setDataFormat(row.getSheet().getWorkbook()
                    .createDataFormat().getFormat("#,##0.00"));
            cell.setCellStyle(style);
        }
    }
}
