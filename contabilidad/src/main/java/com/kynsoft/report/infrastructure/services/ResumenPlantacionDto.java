package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.enums.TipoPlantacion;

import java.math.BigDecimal;

/**
 * DTO para resumen de plantaciones por tipo.
 * Incluye área total en hectáreas, valor total y cantidad de campos.
 */
public class ResumenPlantacionDto {

    private TipoPlantacion tipoPlantacion;
    private BigDecimal areaTotal;
    private BigDecimal valorTotal;
    private Long cantidadCampos;

    public ResumenPlantacionDto() {
    }

    public ResumenPlantacionDto(TipoPlantacion tipoPlantacion, BigDecimal areaTotal,
                                 BigDecimal valorTotal, Long cantidadCampos) {
        this.tipoPlantacion = tipoPlantacion;
        this.areaTotal = areaTotal;
        this.valorTotal = valorTotal;
        this.cantidadCampos = cantidadCampos;
    }

    public TipoPlantacion getTipoPlantacion() {
        return tipoPlantacion;
    }

    public void setTipoPlantacion(TipoPlantacion tipoPlantacion) {
        this.tipoPlantacion = tipoPlantacion;
    }

    public BigDecimal getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(BigDecimal areaTotal) {
        this.areaTotal = areaTotal;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Long getCantidadCampos() {
        return cantidadCampos;
    }

    public void setCantidadCampos(Long cantidadCampos) {
        this.cantidadCampos = cantidadCampos;
    }
}
