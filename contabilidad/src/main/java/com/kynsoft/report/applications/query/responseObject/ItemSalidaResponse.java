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
    private UUID trabajadorId;
    private String trabajadorNombre;
    private Integer cantidad;
    private Double precio;
}
