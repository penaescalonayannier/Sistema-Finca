package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeudaTrabajadorDetalleDto {
    private UUID id;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private String trabajadorRuc;
    private UUID salidaId;
    private String salidaNumero;
    private TipoSalida salidaTipo;
    private UUID productoId;
    private String productoCodigo;
    private String productoNombre;
    private Integer cantidad;
    private Double precioUnitario;
    private Double importe;
    private LocalDateTime fecha;
    private Boolean activo;
    private Boolean pagado;
    private TipoMovimiento tipoMovimiento;
    private FormaPago formaPago;
    private String referenciaBancaria;
    private String observaciones;
}
