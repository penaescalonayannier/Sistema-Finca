package com.kynsoft.report.controller.request;

import com.kynsoft.report.domain.dto.NaturalezaCuenta;
import com.kynsoft.report.domain.dto.TipoCuenta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Request para crear/actualizar una Cuenta Contable o Centro de Costo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CuentaContableRequest {

    private String codigo;
    private String nombre;
    private String descripcion;
    private TipoCuenta tipo;
    private NaturalezaCuenta naturaleza;
    private Integer nivel;
    private UUID cuentaPadreId;
    private Boolean permiteMovimiento;
    private Boolean esCentroCosto;
    private Boolean activo;
}
