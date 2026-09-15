package com.kynsoft.report.infrastructure.services;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para el reporte anual de depreciación.
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP): Formato de reporte de depreciación
 */
public class ReporteDepreciacionDto {

    private UUID activoId;
    private String numeroInventario;
    private String descripcion;
    private String codigoGrupo;
    private BigDecimal valorAdquisicion;
    private BigDecimal depreciacionAnual;
    private BigDecimal depreciacionAcumulada;
    private BigDecimal valorResidual;
    private BigDecimal tasaDepreciacion;

    public ReporteDepreciacionDto() {
    }

    public ReporteDepreciacionDto(UUID activoId, String numeroInventario, String descripcion,
                                   String codigoGrupo, BigDecimal valorAdquisicion,
                                   BigDecimal depreciacionAnual, BigDecimal depreciacionAcumulada,
                                   BigDecimal valorResidual, BigDecimal tasaDepreciacion) {
        this.activoId = activoId;
        this.numeroInventario = numeroInventario;
        this.descripcion = descripcion;
        this.codigoGrupo = codigoGrupo;
        this.valorAdquisicion = valorAdquisicion;
        this.depreciacionAnual = depreciacionAnual;
        this.depreciacionAcumulada = depreciacionAcumulada;
        this.valorResidual = valorResidual;
        this.tasaDepreciacion = tasaDepreciacion;
    }

    public UUID getActivoId() {
        return activoId;
    }

    public void setActivoId(UUID activoId) {
        this.activoId = activoId;
    }

    public String getNumeroInventario() {
        return numeroInventario;
    }

    public void setNumeroInventario(String numeroInventario) {
        this.numeroInventario = numeroInventario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigoGrupo() {
        return codigoGrupo;
    }

    public void setCodigoGrupo(String codigoGrupo) {
        this.codigoGrupo = codigoGrupo;
    }

    public BigDecimal getValorAdquisicion() {
        return valorAdquisicion;
    }

    public void setValorAdquisicion(BigDecimal valorAdquisicion) {
        this.valorAdquisicion = valorAdquisicion;
    }

    public BigDecimal getDepreciacionAnual() {
        return depreciacionAnual;
    }

    public void setDepreciacionAnual(BigDecimal depreciacionAnual) {
        this.depreciacionAnual = depreciacionAnual;
    }

    public BigDecimal getDepreciacionAcumulada() {
        return depreciacionAcumulada;
    }

    public void setDepreciacionAcumulada(BigDecimal depreciacionAcumulada) {
        this.depreciacionAcumulada = depreciacionAcumulada;
    }

    public BigDecimal getValorResidual() {
        return valorResidual;
    }

    public void setValorResidual(BigDecimal valorResidual) {
        this.valorResidual = valorResidual;
    }

    public BigDecimal getTasaDepreciacion() {
        return tasaDepreciacion;
    }

    public void setTasaDepreciacion(BigDecimal tasaDepreciacion) {
        this.tasaDepreciacion = tasaDepreciacion;
    }
}
