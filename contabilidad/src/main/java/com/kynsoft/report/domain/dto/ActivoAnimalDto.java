package com.kynsoft.report.domain.dto;

import com.kynsoft.report.domain.dto.enums.CategoriaAnimal;
import com.kynsoft.report.domain.dto.enums.TipoGanado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para Activo Animal (Grupo 08 de AFT).
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP): Grupo 08 - Animales
 * - Certificación de Tenencia de Ganado Mayor
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivoAnimalDto {

    private UUID id;
    private String numeroInventario;
    private String codigoArete;
    private String hierro;
    private CategoriaAnimal categoria;
    private TipoGanado tipoGanado;
    private UUID fincaId;
    private String fincaNombre;
    private BigDecimal valorAdquisicion;
    private BigDecimal depreciacionAcumulada;
    private BigDecimal valorResidual;
    private BigDecimal valorTasacion;
    private Integer aniosVida;
    private BigDecimal pesoPromedio;
    private String destino;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Obtiene el nombre legible de la categoría.
     */
    public String getCategoriaNombre() {
        return categoria != null ? categoria.getNombre() : null;
    }

    /**
     * Obtiene el nombre legible del tipo de ganado.
     */
    public String getTipoGanadoNombre() {
        return tipoGanado != null ? tipoGanado.getNombre() : null;
    }
}
