package com.kynsoft.report.applications.query.tomaprestamo.respaldoCultural.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespaldoCulturalDto {
    private Long id;
    private LocalDate fecha;
    private String empresa;
    private String apa;
    private String upc;
    private String numeroCuenta;
    private String sucursalBancaria;
    private BigDecimal toneladasCana;
    private BigDecimal aprobado;
    private BigDecimal efectivoAprobado;
    private BigDecimal noEfectivoAprobado;
    private BigDecimal efectivoUtilizado;
    private BigDecimal noEfectivoUtilizado;
    private BigDecimal efectivoDisponible;
    private BigDecimal noEfectivoDisponible;
    private BigDecimal subtotalNoEfectivo;
    private BigDecimal subtotalEfectivo;
    private BigDecimal totalEfectivoUtilizado;
    private BigDecimal totalNoEfectivoUtilizado;
    private BigDecimal totalEfectivoDisponible;
    private BigDecimal totalNoEfectivoDisponible;
    private String economicoUPC;
    private LocalDate fechaFirmaAPA;
    private LocalDate fechaFirmaUPC;
    
    private List<DistribucionDto> distribucionesNoEfectivo;
    private List<DistribucionDto> distribucionesEfectivo;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DistribucionDto {
    private String concepto;
    private BigDecimal valor;
}