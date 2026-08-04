package com.kynsoft.report.applications.query.responseObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorasExcedidasSummaryResponse {
    private String trabajadorId;
    private String nombre;
    private String ruc;
    private String cargo;
    private Integer diasExcedidos;
    private Double totalHorasExcedidas;
    private List<String> diasConExceso; // Formato: "2024-01-15"
}
