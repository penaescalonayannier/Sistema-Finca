package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineaSalidaMultipleAlmacenDto {
    private UUID almacenFincaProductoId;
    private Integer cantidad;
    /** Compradores del producto cuando el destino de la salida es TRABAJADORES. */
    private List<ItemSalidaDto> items;
}
