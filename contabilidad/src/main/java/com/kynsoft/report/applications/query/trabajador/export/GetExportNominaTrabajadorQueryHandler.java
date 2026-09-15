package com.kynsoft.report.applications.query.trabajador.export;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.report.estadoCuenta.export.Response;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.ITrabajadorService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class GetExportNominaTrabajadorQueryHandler implements IQueryHandler<GetExportNominaTrabajadorQuery, Response> {

    private final ITrabajadorService service;

    public GetExportNominaTrabajadorQueryHandler(ITrabajadorService service) {
        this.service = service;
    }

    @Override
    public Response handle(GetExportNominaTrabajadorQuery query) {
        List<TrabajadorDto> trabajadores = service.findAll(query.getTrabajadoresIds());
        
        // Crear lista mutable y ordenar
        List<TrabajadorDto> trabajadoresOrdenados = new ArrayList<>(trabajadores);
        trabajadoresOrdenados.sort(Comparator.comparing(TrabajadorDto::getNombre, String.CASE_INSENSITIVE_ORDER));

        return Response.builder()
                .outputStream(createStreamingResponse(trabajadoresOrdenados))
                .build();
    }

    private StreamingResponseBody createStreamingResponse(List<TrabajadorDto> trabajadores) {
        return outputStream -> {
            try (Workbook workbook = new HSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Nomina");

                String[] headers = {"NOMBRE", "NID", "CUENTA", "IMPORTE"};
                Row headerRow = sheet.createRow(0);

                CellStyle headerStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);

                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                int rowNum = 1;
                for (TrabajadorDto t : trabajadores) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(t.getNombre());
                    row.createCell(1).setCellValue(t.getRuc());
                    row.createCell(2).setCellValue(t.getCuenta());
                    row.createCell(3).setCellValue("");
                }

                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(outputStream);
            } catch (Exception e) {
                throw new RuntimeException("Error generando el excel de nómina", e);
            }
        };
    }
}