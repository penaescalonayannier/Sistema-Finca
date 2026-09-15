package com.kynsoft.report.applications.query.report.estadoCuenta.export;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.domain.dto.ClienteDto;
import com.kynsoft.report.domain.dto.EstadoCuentaDto;
import com.kynsoft.report.domain.services.IClienteService;
import com.kynsoft.report.domain.services.IEstadoCuentaService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GetExportEstadoCuentaQueryHandler implements IQueryHandler<GetExportEstadoCuentaQuery, Response> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final IEstadoCuentaService estadoCuentaService;
    private final IClienteService clienteService;

    public GetExportEstadoCuentaQueryHandler(IEstadoCuentaService estadoCuentaService,
                                              IClienteService clienteService) {
        this.estadoCuentaService = estadoCuentaService;
        this.clienteService = clienteService;
    }

    @Override
    public Response handle(GetExportEstadoCuentaQuery query) {
        // 1. Obtener todos los datos filtrados (no paginados)
        List<EstadoCuentaDto> allData = estadoCuentaService.findAllByDate(
                query.getFechaInicio(),
                query.getFechaFin()
        );

        // 2. Agrupar los datos por tipo (Cr o Db)
        Map<String, List<EstadoCuentaDto>> groupedData = allData.stream()
                .filter(d -> d.getTipo() != null)
                .collect(Collectors.groupingBy(EstadoCuentaDto::getTipo));

        List<EstadoCuentaDto> creditos = groupedData.getOrDefault("Cr", List.of());
        List<EstadoCuentaDto> debitos = groupedData.getOrDefault("Db", List.of());

        return Response.builder().outputStream(this.response(creditos, debitos)).build();
    }

    private StreamingResponseBody response(List<EstadoCuentaDto> creditos, List<EstadoCuentaDto> debitos) {
        return outputStream -> {
            try (Workbook workbook = new XSSFWorkbook()) {

                // Generar hoja para Créditos
                createSheet(workbook, "CREDITOS (Cr)", creditos);

                // Generar hoja para Débitos
                createSheet(workbook, "DEBITOS (Db)", debitos);

                // Escribir el libro de trabajo en el flujo de respuesta
                workbook.write(outputStream);
            } catch (Exception e) {
                // Manejo de errores
                throw new RuntimeException("Error al generar el archivo Excel", e);
            }
        };
    }

    // Método auxiliar para crear una hoja de cálculo
    private void createSheet(Workbook workbook, String sheetName, List<EstadoCuentaDto> data) {
        Sheet sheet = workbook.createSheet(sheetName);

        // Estilo de cabecera
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Estilo para celdas con ajuste de texto
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);

        // Estilo para celda de total (negrita)
        CellStyle totalStyle = workbook.createCellStyle();
        Font totalFont = workbook.createFont();
        totalFont.setBold(true);
        totalStyle.setFont(totalFont);
        totalStyle.setAlignment(HorizontalAlignment.RIGHT);

        // Estilo para celda de total con formato de número (negrita)
        CellStyle totalNumberStyle = workbook.createCellStyle();
        totalNumberStyle.setFont(totalFont);
        // Si quieres formato de moneda, puedes agregar:
        // totalNumberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

        // Eliminado "ID" de los encabezados
        String[] headers = {"Fecha", "Ref. Origen", "Ref. Corriente", "Cliente", "Observaciones", "Importe", "Tipo"};

        // Fila de Cabecera
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Filas de Datos
        int rowNum = 1;
        double totalImporte = 0.0;
        
        for (EstadoCuentaDto dto : data) {
            Row row = sheet.createRow(rowNum++);

            // Eliminada la columna ID (índice 0)
            int colIndex = 0;
            row.createCell(colIndex++).setCellValue(dto.getFecha() != null ? dto.getFecha().format(DATE_FORMATTER) : "");
            row.createCell(colIndex++).setCellValue(dto.getRefOrigen());
            row.createCell(colIndex++).setCellValue(dto.getRefCorriente());

            // Obtener el nombre del cliente
            String clienteNombre = "";
            if (dto.getClienteId() != null) {
                try {
                    ClienteDto cliente = clienteService.findById(dto.getClienteId());
                    clienteNombre = cliente.getNombre() != null ? cliente.getNombre() : "";
                } catch (Exception e) {
                    clienteNombre = "";
                }
            }
            row.createCell(colIndex++).setCellValue(clienteNombre);

            // Celda de observaciones con estilo de ajuste de texto
            Cell observacionesCell = row.createCell(colIndex++);
            observacionesCell.setCellValue(dto.getObservaciones());
            observacionesCell.setCellStyle(wrapStyle);

            // Obtener el importe y sumarlo al total
            Double importe = dto.getImporte() != null ? dto.getImporte() : 0.0;
            row.createCell(colIndex++).setCellValue(importe);
            totalImporte += importe;

            row.createCell(colIndex++).setCellValue(dto.getTipo());
        }

        // Fila de Total
        if (!data.isEmpty()) {
            Row totalRow = sheet.createRow(rowNum);
            
            // Celda "TOTAL" alineada a la derecha en la columna "Cliente" (índice 3)
            Cell totalLabelCell = totalRow.createCell(3);
            totalLabelCell.setCellValue("TOTAL");
            totalLabelCell.setCellStyle(totalStyle);
            
            // Celda con el total en la columna "Importe" (índice 5)
            Cell totalValueCell = totalRow.createCell(5);
            totalValueCell.setCellValue(totalImporte);
            totalValueCell.setCellStyle(totalNumberStyle);
            
            // También puedes combinar celdas para que "TOTAL" aparezca centrado sobre varias columnas
            // sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 3));
            // Cell totalLabelCell = totalRow.createCell(0);
            // totalLabelCell.setCellValue("TOTAL");
            // totalLabelCell.setCellStyle(totalStyle);
        }

        // Ajuste automático de ancho de columnas para todas excepto observaciones
        for (int i = 0; i < headers.length; i++) {
            if (i != 4) { // Ahora la columna de observaciones es la 4 (índice 4)
                sheet.autoSizeColumn(i);
            }
        }

        // Establecer un ancho fijo para la columna de observaciones (columna 4)
        // 50 unidades = aproximadamente 50 caracteres
        sheet.setColumnWidth(4, 50 * 256);
    }
}