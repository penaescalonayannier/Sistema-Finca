package com.kynsoft.report.applications.query.responseObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemSalidaResponse {
    private UUID id;
    private UUID salidaId;
    private UUID fincaProductoId;
    private UUID almacenFincaProductoId;
    private String productoCode;
    private String productoName;
    private String unidadMedida;
    private UUID trabajadorId;
    private String trabajadorNombre;
    private UUID laborId;
    private String laborCode;
    private String laborName;
    private Double cantidadActividad;
    private Double indiceConsumoCalculado;
    private Double indiceConsumoPermisible;
    private Double cantidad;
    private Double precio;
}
