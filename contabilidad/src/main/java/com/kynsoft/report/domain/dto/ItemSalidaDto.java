package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSalidaDto {
    private UUID id;
    private UUID salidaId;
    private UUID fincaProductoId;
    private UUID almacenFincaProductoId;
    private String productoCode;
    private String productoName;
    private String unidadMedida;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private Integer cantidad;
    private Double precio;
    private Boolean pagado;
}
