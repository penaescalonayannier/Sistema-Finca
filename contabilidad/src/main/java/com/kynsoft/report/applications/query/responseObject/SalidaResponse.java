package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.DestinoSalida;
import com.kynsoft.report.domain.dto.TipoSalida;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalidaResponse implements IResponse {
    private UUID id;
    private TipoSalida tipo;
    private DestinoSalida destino;
    private String numero;
    private UUID fincaProductoId;
    private String fincaCode;
    private String fincaName;
    private String productoCode;
    private String productoName;
    private Double stockActual;
    private LocalDateTime fecha;
    private String observaciones;
    private Double cantidadTotal;
    private List<ItemSalidaResponse> items;

    // Constructor sin items (para listados)
    public SalidaResponse(UUID id, TipoSalida tipo, DestinoSalida destino, String numero, UUID fincaProductoId,
                          String fincaCode, String fincaName, String productoCode, String productoName,
                          Double stockActual, LocalDateTime fecha, String observaciones, Double cantidadTotal) {
        this.id = id;
        this.tipo = tipo;
        this.destino = destino;
        this.numero = numero;
        this.fincaProductoId = fincaProductoId;
        this.fincaCode = fincaCode;
        this.fincaName = fincaName;
        this.productoCode = productoCode;
        this.productoName = productoName;
        this.stockActual = stockActual;
        this.fecha = fecha;
        this.observaciones = observaciones;
        this.cantidadTotal = cantidadTotal;
        this.items = null;
    }
}
