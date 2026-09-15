package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO para Regla de Contabilización.
 * Mapea TipoMovimientoStock → Cuentas Débito/Crédito
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReglaContabilizacionDto {

    private UUID id;

    // Condiciones del movimiento físico
    private TipoMovimientoStock tipoMovimiento;
    private UUID almacenId;
    private UUID fincaId;
    private String tipoProducto;

    // Cuentas resultantes
    private String cuentaDebito;
    private String cuentaCredito;

    // Centro de costo (opcional)
    private String centroCostoDebito;
    private String centroCostoCredito;

    // Configuración
    private String descripcionPlantilla;
    private Integer prioridad;
    private Boolean activo;
}
