package com.kynsoft.report.infrastructure.services;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class PrenominaExcelService {

    @Value("${prenomina.excel.path:/media/yannier/39A7FE8A2172FB36/Trabajo/Sistema Finca/contabilidad/src/main/resources/}")
    private String prenominaBasePath;

    @Value("${prenomina.excel.filename:PRENOMINA SALARIO BANDEC FINCA LORENZO DALIS MANATI.xls}")
    private String prenominaFilename;

    // Columna donde está el RUC (ajustar según el Excel real)
    private static final int COLUMNA_RUC = 2; // Columna C (índice 2)

    // Columna donde se escribirán las horas
    private static final int COLUMNA_HORAS = 10; // Columna K (índice 10)

    /**
     * Escribe las horas del consolidado en el archivo Excel de prenómina.
     * - Lee la plantilla original
     * - Escribe las horas en la columna K
     * - Devuelve los bytes del archivo modificado para descarga
     * - Limpia la columna K de la plantilla original
     *
     * @param horasPorRuc Mapa de RUC -> Horas totales del mes
     * @return Resultado con los bytes del archivo y estadísticas
     */
    public WriteResult escribirHorasEnPrenomina(Map<String, Double> horasPorRuc) throws IOException {
        Path filePath = Paths.get(prenominaBasePath, prenominaFilename);

        int actualizados = 0;
        int noEncontrados = 0;
        List<String> rucsNoEncontrados = new ArrayList<>();
        List<String> rucsActualizados = new ArrayList<>();
        byte[] archivoBytes;

        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             HSSFWorkbook workbook = new HSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Primera hoja

            // Crear un mapa de RUC -> Fila para búsqueda rápida
            Map<String, Integer> rucToRowMap = new HashMap<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell rucCell = row.getCell(COLUMNA_RUC);
                if (rucCell != null) {
                    String ruc = getCellValueAsString(rucCell).trim();
                    if (!ruc.isEmpty()) {
                        rucToRowMap.put(ruc, i);
                    }
                }
            }

            // Para cada trabajador del consolidado, buscar y escribir las horas
            for (Map.Entry<String, Double> entry : horasPorRuc.entrySet()) {
                String ruc = entry.getKey();
                Double horas = entry.getValue();

                if (ruc == null || ruc.isEmpty()) continue;

                Integer rowIndex = rucToRowMap.get(ruc);

                if (rowIndex == null) {
                    // Intentar buscar sin ceros a la izquierda o con formato diferente
                    rowIndex = buscarRucAlternativo(rucToRowMap, ruc);
                }

                if (rowIndex != null) {
                    Row row = sheet.getRow(rowIndex);
                    if (row != null) {
                        // Obtener la celda existente o crearla si no existe
                        Cell horasCell = row.getCell(COLUMNA_HORAS, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        // Escribir el valor
                        horasCell.setCellValue(horas);
                        actualizados++;
                        rucsActualizados.add(ruc + " -> " + horas + "h");
                    }
                } else {
                    noEncontrados++;
                    rucsNoEncontrados.add(ruc);
                }
            }

            // Convertir el workbook modificado a bytes para descarga
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                workbook.write(baos);
                archivoBytes = baos.toByteArray();
            }
        }

        // Limpiar la columna K de la plantilla original
        limpiarColumnaHorasPlantilla();

        return new WriteResult(actualizados, noEncontrados, rucsNoEncontrados, rucsActualizados, horasPorRuc.size(), archivoBytes);
    }

    /**
     * Limpia la columna K (horas) de la plantilla original para reutilizarla
     * Solo borra el contenido, preserva el formato de las celdas
     */
    private void limpiarColumnaHorasPlantilla() throws IOException {
        Path filePath = Paths.get(prenominaBasePath, prenominaFilename);

        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             HSSFWorkbook workbook = new HSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Limpiar solo el contenido de la columna K (excepto encabezado)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell horasCell = row.getCell(COLUMNA_HORAS);
                if (horasCell != null) {
                    // Solo borrar el valor, no tocar el estilo
                    horasCell.setCellValue("");
                }
            }

            // Guardar la plantilla limpia
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                workbook.write(fos);
            }
        }
    }

    /**
     * Busca el RUC con formatos alternativos (sin ceros a la izquierda, etc.)
     */
    private Integer buscarRucAlternativo(Map<String, Integer> rucToRowMap, String ruc) {
        // Intentar sin ceros a la izquierda
        String rucSinCeros = ruc.replaceFirst("^0+", "");
        if (rucToRowMap.containsKey(rucSinCeros)) {
            return rucToRowMap.get(rucSinCeros);
        }

        // Intentar con ceros a la izquierda hasta 11 dígitos
        try {
            String rucConCeros = String.format("%011d", Long.parseLong(ruc.replaceAll("\\D", "")));
            if (rucToRowMap.containsKey(rucConCeros)) {
                return rucToRowMap.get(rucConCeros);
            }
        } catch (NumberFormatException e) {
            // Ignorar si no es un número válido
        }

        // Buscar coincidencia parcial
        for (String key : rucToRowMap.keySet()) {
            if (key.contains(ruc) || ruc.contains(key)) {
                return rucToRowMap.get(key);
            }
        }

        return null;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                double numValue = cell.getNumericCellValue();
                if (numValue == Math.floor(numValue)) {
                    return String.valueOf((long) numValue);
                }
                return String.valueOf(numValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf((long) cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            default:
                return "";
        }
    }

    // Clase para el resultado de la escritura
    public static class WriteResult {
        private final int actualizados;
        private final int noEncontrados;
        private final List<String> rucsNoEncontrados;
        private final List<String> rucsActualizados;
        private final int totalEnConsolidado;
        private final byte[] archivoBytes;

        public WriteResult(int actualizados, int noEncontrados, List<String> rucsNoEncontrados,
                          List<String> rucsActualizados, int totalEnConsolidado, byte[] archivoBytes) {
            this.actualizados = actualizados;
            this.noEncontrados = noEncontrados;
            this.rucsNoEncontrados = rucsNoEncontrados;
            this.rucsActualizados = rucsActualizados;
            this.totalEnConsolidado = totalEnConsolidado;
            this.archivoBytes = archivoBytes;
        }

        public int getActualizados() { return actualizados; }
        public int getNoEncontrados() { return noEncontrados; }
        public List<String> getRucsNoEncontrados() { return rucsNoEncontrados; }
        public List<String> getRucsActualizados() { return rucsActualizados; }
        public int getTotalEnConsolidado() { return totalEnConsolidado; }
        public byte[] getArchivoBytes() { return archivoBytes; }
    }
}
