package com.kynsoft.report.applications.command.report.trabajador.upload;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.ITrabajadorService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Component
public class ImportTrabajadorCsvCommandHandler implements ICommandHandler<ImportTrabajadorCsvCommand> {

    private final ITrabajadorService serviceImpl;

    public ImportTrabajadorCsvCommandHandler(ITrabajadorService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    @Transactional
    public void handle(ImportTrabajadorCsvCommand command) {
        // Lista para recolectar los DTOs de trabajadores importados
        List<TrabajadorImportadoDto> trabajadoresImportados = new ArrayList<>();

        try (ByteArrayInputStream bis = new ByteArrayInputStream(command.getFileContent()); Workbook workbook = new XSSFWorkbook(bis)) {

            // Obtener la primera hoja del libro de Excel
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Omitir la cabecera (primera fila)
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            // Procesar cada fila
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Obtener los valores de las celdas
                String ci_trabajador = getCellValue(row.getCell(0));
                String nombre_trabajador = getCellValue(row.getCell(1));
                String cuentaE_trabajador = getCellValue(row.getCell(2));

                // Validar que los campos requeridos no estén vacíos
                if ((ci_trabajador == null || ci_trabajador.trim().isEmpty())
                        || (nombre_trabajador == null || nombre_trabajador.trim().isEmpty())) {
                    continue; // Saltar filas con datos incompletos
                }

                UUID newId = UUID.randomUUID();

                // 1. Mapear a TrabajadorDto (para persistir)
                TrabajadorDto trabajadorDto = TrabajadorDto.builder()
                        .id(newId)
                        .ruc(ci_trabajador.trim())
                        .nombre(nombre_trabajador.trim())
                        .cuenta(cuentaE_trabajador != null ? cuentaE_trabajador.trim() : "")
                        .build();

                serviceImpl.create(trabajadorDto);

                // 2. Recolectar la data del trabajador importado para el mensaje
                TrabajadorImportadoDto resultado = new TrabajadorImportadoDto(
                        newId,
                        ci_trabajador.trim(),
                        nombre_trabajador.trim(),
                        cuentaE_trabajador != null ? cuentaE_trabajador.trim() : ""
                );
                trabajadoresImportados.add(resultado);
            }

            // 3. Establecer la lista de resultados en el Command antes de finalizar
            command.setResultados(trabajadoresImportados);

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el archivo Excel de Trabajadores", e);
        }
    }

    /**
     * Método auxiliar para obtener el valor de una celda según su tipo
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Si es una fecha, formatearla
                    return cell.getDateCellValue().toString();
                } else {
                    // Si es número, convertirlo a String sin notación científica
                    double num = cell.getNumericCellValue();
                    // Verificar si es un número entero
                    if (num == Math.floor(num)) {
                        return String.valueOf((long) num);
                    } else {
                        return String.valueOf(num);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Evaluar la fórmula
                try {
                return getCellValue(cell.getSheet().getWorkbook()
                        .getCreationHelper()
                        .createFormulaEvaluator()
                        .evaluateInCell(cell));
            } catch (Exception e) {
                return cell.getCellFormula();
            }
            case BLANK:
                return "";
            default:
                return "";
        }
    }

}
