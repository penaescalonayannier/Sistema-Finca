package com.kynsoft.report.applications.query.responseObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductividadResponse {
    private String trabajadorId;
    private String nombre;
    private String ruc;
    private String cargo;
    private String cuenta;
    private Double totalHoras;
    private Double normaEsperada;
    private Double porcentajeCumplimiento;
    private Double horasPromedioDia;
    private Double variabilidad; // Desviación estándar
    private String consistencia; // ALTA, MEDIA, BAJA
    private Integer diasTrabajados;
}
