package com.kynsoft.report.applications.command.producto.importexcel;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.ProductoDto;
import com.kynsoft.report.domain.dto.UnidadMedida;
import com.kynsoft.report.domain.services.IProductoService;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class ImportProductoExcelCommandHandler implements ICommandHandler<ImportProductoExcelCommand> {

    private final IProductoService productoService;

    // Patrón para validar código alfanumérico (solo letras y números)
    private static final Pattern CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    @Override
    public void handle(ImportProductoExcelCommand command) {
        ImportProductoExcelMessage message = (ImportProductoExcelMessage) command.getMessage();

        try (InputStream inputStream = command.getExcelInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = 0;

            for (Row row : sheet) {
                // Saltar la fila de encabezados
                if (rowNum == 0) {
                    rowNum++;
                    continue;
                }

                try {
                    ProductoDto producto = parseRow(row, rowNum);
                    if (producto != null) {
                        productoService.create(producto);
                        message.getProductosCreados().add(producto.getId());
                        message.setTotalImportados(message.getTotalImportados() + 1);
                    }
                } catch (Exception e) {
                    message.setTotalErrores(message.getTotalErrores() + 1);
                    message.getErrores().add("Fila " + (rowNum + 1) + ": " + e.getMessage());
                }

                rowNum++;
            }

        } catch (Exception e) {
            message.setTotalErrores(message.getTotalErrores() + 1);
            message.getErrores().add("Error al procesar el archivo: " + e.getMessage());
        }
    }

    private ProductoDto parseRow(Row row, int rowNum) {
        // Orden: name, unidadMedida, code, priceTrabajador, priceComedor, price, description
        String name = getCellStringValue(row.getCell(0));
        String unidadMedidaStr = getCellStringValue(row.getCell(1));
        String code = getCellStringValue(row.getCell(2));
        Double priceTrabajador = getCellDoubleValue(row.getCell(3));
        Double priceComedor = getCellDoubleValue(row.getCell(4));
        Double price = getCellDoubleValue(row.getCell(5));
        String description = getCellStringValue(row.getCell(6));

        // Validaciones según la especificación

        // RN-04: Código alfanumérico
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("El código es requerido");
        }
        if (!CODE_PATTERN.matcher(code.trim()).matches()) {
            throw new IllegalArgumentException("El código solo puede contener letras y números (sin espacios ni caracteres especiales)");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }

        if (unidadMedidaStr == null || unidadMedidaStr.trim().isEmpty()) {
            throw new IllegalArgumentException("La unidad de medida es requerida");
        }

        // Convertir String a enum UnidadMedida
        UnidadMedida unidadMedida;
        try {
            unidadMedida = UnidadMedida.valueOf(unidadMedidaStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unidad de medida inválida: " + unidadMedidaStr + ". Valores válidos: KG, G, LB, QQ, L, ML, GAL, UND, DOC, SACO, CAJA, M, CM");
        }

        // RN-01: Precios > 0
        if (price == null) {
            throw new IllegalArgumentException("El precio es requerido");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }

        if (priceTrabajador == null) {
            throw new IllegalArgumentException("El precio trabajador es requerido");
        }
        if (priceTrabajador <= 0) {
            throw new IllegalArgumentException("El precio trabajador debe ser mayor a 0");
        }

        if (priceComedor == null) {
            throw new IllegalArgumentException("El precio comedor es requerido");
        }
        if (priceComedor <= 0) {
            throw new IllegalArgumentException("El precio comedor debe ser mayor a 0");
        }

        return ProductoDto.builder()
                .id(UUID.randomUUID())
                .code(code.trim())
                .name(name.trim())
                .price(price)
                .unidadMedida(unidadMedida)
                .description(description != null ? description.trim() : "")
                .priceTrabajador(priceTrabajador)
                .priceComedor(priceComedor)
                .stock(0)
                .active(true)
                .build();
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private Double getCellDoubleValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().replace(",", "."));
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }
}
