package com.kynsoft.report.applications.query.responseObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrabajadorRankingResponse {
    private Integer ranking;
    private String trabajadorId;
    private String nombre;
    private String ruc;
    private String cargo;
    private Double indiceProductividad;
    private Double totalHoras;
    private Double porcentajeCumplimiento;
}
