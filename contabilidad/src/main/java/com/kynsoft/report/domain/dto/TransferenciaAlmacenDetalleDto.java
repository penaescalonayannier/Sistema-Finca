package com.kynsoft.report.domain.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Builder
public class TransferenciaAlmacenDetalleDto {
    private UUID id;
    private UUID fincaId;
    private String numeroDocumento;
    private UUID origenAlmacenId;
    private String origenAlmacenNombre;
    private UUID destinoAlmacenId;
    private String destinoAlmacenNombre;
    private EstadoTransferenciaAlmacen estado;
    private LocalDateTime fechaDespacho;
    private LocalDateTime fechaRecepcion;
    private String observaciones;
    private String motivoCierre;
    private List<Linea> lineas;
    @Getter @Builder
    public static class Linea {
        private UUID id;
        private UUID fincaProductoId;
        private String productoNombre;
        private String unidadMedida;
        private Double cantidadDespachada;
        private Double cantidadRecibida;
        private Double cantidadRechazada;
        private String observaciones;
    }
}
