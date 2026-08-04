package com.kynsoft.report.applications.query.responseObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AbsentismoResponse {
    private String trabajadorId;
    private String nombre;
    private String ruc;
    private String cargo;
    private String cuenta;
    private Integer diasLaborables;
    private Integer diasTrabajados;
    private Integer diasFaltados;
    private Double porcentajeAsistencia;
    private String patron; // CONSECUTIVO, OCASIONAL, VIERNES_LUNES, ALEATORIO
    private String tendencia; // MEJORANDO, EMPEORANDO, ESTABLE
}
