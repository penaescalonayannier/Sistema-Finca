package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO para Cuenta Contable según Nomenclador Cubano (Res. 494/2016 MFP)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaContableDto {

    private UUID id;
    private String codigo;
    private String nombre;
    private TipoCuenta tipo;
    private NaturalezaCuenta naturaleza;
    private Integer nivel;
    private UUID cuentaPadreId;
    private String cuentaPadreCodigo;
    private String cuentaPadreNombre;
    private String descripcion;
    private Boolean permiteMovimiento;
    private Boolean esCentroCosto;
    private Boolean activo;
}
