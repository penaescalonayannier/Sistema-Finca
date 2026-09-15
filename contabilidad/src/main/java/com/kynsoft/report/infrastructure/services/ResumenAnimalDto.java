package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.enums.CategoriaAnimal;

import java.math.BigDecimal;

/**
 * DTO para resumen de animales por categoría.
 * Usado en reportes de inventario ganadero.
 */
public class ResumenAnimalDto {

    private CategoriaAnimal categoria;
    private Long cantidad;
    private BigDecimal valorTotal;

    public ResumenAnimalDto() {
    }

    public ResumenAnimalDto(CategoriaAnimal categoria, Long cantidad, BigDecimal valorTotal) {
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.valorTotal = valorTotal;
    }

    public CategoriaAnimal getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaAnimal categoria) {
        this.categoria = categoria;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
