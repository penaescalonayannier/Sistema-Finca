package com.kynsoft.report.domain.dto.seccionesGenericas;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TableRowData {
    private String[] cellData;
    private boolean splittable;
    
    // Constructor conveniente para filas no divisibles
    public static TableRowData of(String... cellData) {
        return TableRowData.builder()
                .cellData(cellData)
                .splittable(false)
                .build();
    }
    
    // Constructor conveniente para filas divisibles
    public static TableRowData splittable(String... cellData) {
        return TableRowData.builder()
                .cellData(cellData)
                .splittable(true)
                .build();
    }
}