package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProduccionTerminadaDto {
    private UUID id;
    private UUID fincaId;
    private String fincaCode;
    private String fincaName;
    private UUID productoId;
    private String productoCode;
    private String productoName;
    private LocalDateTime fecha;
    private Double cantidadTerminada;
    private UUID trabajadorEntregaId;
    private String trabajadorEntregaNombre;
    private UUID trabajadorRecibeId;
    private String trabajadorRecibeNombre;
    private String observaciones;
    private Boolean activo;

    /** Datos congelados del SC-2-06, independientes del catálogo actual. */
    private String numeroDocumento;
    private String productoCodigoSnapshot;
    private String productoNombreSnapshot;
    private String unidadMedidaSnapshot;
    private String almacenNombreSnapshot;
    private String almacenInventarioSnapshot;
    private Double costoUnitario;
    private Double importe;
    private Double saldoPosterior;
    private String lote;
    private String centroCosto;

    /** Almacén que recibió físicamente la producción, cuando fue registrada desde una entrada. */
    private UUID almacenFincaProductoId;

    public void setCantidadTerminada(Number cantidadTerminada) {
        this.cantidadTerminada = cantidadTerminada != null ? cantidadTerminada.doubleValue() : null;
    }

    public static class ProduccionTerminadaDtoBuilder {
        public ProduccionTerminadaDtoBuilder cantidadTerminada(Number cantidadTerminada) {
            this.cantidadTerminada = cantidadTerminada != null ? cantidadTerminada.doubleValue() : null;
            return this;
        }
    }
}
