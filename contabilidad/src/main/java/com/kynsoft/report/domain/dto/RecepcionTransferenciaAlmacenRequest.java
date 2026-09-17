package com.kynsoft.report.domain.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

/** Cantidades constatadas por el receptor del almacén destino. */
@Getter @Setter
public class RecepcionTransferenciaAlmacenRequest {
    private List<Linea> lineas;
    private String observaciones;
    @Getter @Setter
    public static class Linea {
        private UUID lineaId;
        private Double cantidadRecibida;
        private String observaciones;
    }
}
