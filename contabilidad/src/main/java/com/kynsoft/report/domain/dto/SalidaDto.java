package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalidaDto {
    private UUID id;
    private TipoSalida tipo;
    private DestinoSalida destino;
    private String numero;
    private UUID fincaProductoId;
    private UUID almacenFincaProductoId; // Optional: when creating from almacen view
    private String fincaCode;
    private String fincaName;
    private String productoCode;
    private String productoName;
    private String unidadMedida;
    private Double stockActual;
    private LocalDateTime fecha;
    private String observaciones;
    private Integer cantidadTotal;
    private List<ItemSalidaDto> items;
    private Boolean activo;
}
