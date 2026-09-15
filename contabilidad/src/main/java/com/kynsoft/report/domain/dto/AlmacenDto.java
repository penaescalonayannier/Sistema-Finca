package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AlmacenDto {
    private UUID id;
    private String nombre;
    private String descripcion;
    private String inventario;
    private Boolean esPrincipal;
    private Boolean activo;
    private UUID fincaId;
    private String fincaCode;
    private String fincaName;
    private List<FincaProductoDto> productos;
    private Long cantidadProductos;
    private Long stockTotal;
}
