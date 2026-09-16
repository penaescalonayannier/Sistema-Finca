package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.ProduccionTerminadaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProduccionTerminadaResponse implements IResponse {
    private UUID id;
    private UUID fincaId;
    private String fincaCode;
    private String fincaName;
    private UUID productoId;
    private String productoCode;
    private String productoName;
    private LocalDateTime fecha;
    private Double cantidadTerminada;
    private UUID almacenFincaProductoId;
    private UUID trabajadorEntregaId;
    private String trabajadorEntregaNombre;
    private UUID trabajadorRecibeId;
    private String trabajadorRecibeNombre;
    private String observaciones;
    private String numeroDocumento;
    private String almacenNombre;
    private String almacenInventario;
    private Double costoUnitario;
    private Double importe;
    private Double saldoPosterior;
    private String lote;
    private String centroCosto;
    private Boolean activo;

    public ProduccionTerminadaResponse(ProduccionTerminadaDto dto) {
        this.id = dto.getId();
        this.fincaId = dto.getFincaId();
        this.fincaCode = dto.getFincaCode();
        this.fincaName = dto.getFincaName();
        this.productoId = dto.getProductoId();
        this.productoCode = dto.getProductoCode();
        this.productoName = dto.getProductoName();
        this.fecha = dto.getFecha();
        this.cantidadTerminada = dto.getCantidadTerminada();
        this.almacenFincaProductoId = dto.getAlmacenFincaProductoId();
        this.trabajadorEntregaId = dto.getTrabajadorEntregaId();
        this.trabajadorEntregaNombre = dto.getTrabajadorEntregaNombre();
        this.trabajadorRecibeId = dto.getTrabajadorRecibeId();
        this.trabajadorRecibeNombre = dto.getTrabajadorRecibeNombre();
        this.observaciones = dto.getObservaciones();
        this.numeroDocumento = dto.getNumeroDocumento();
        this.almacenNombre = dto.getAlmacenNombreSnapshot();
        this.almacenInventario = dto.getAlmacenInventarioSnapshot();
        this.costoUnitario = dto.getCostoUnitario();
        this.importe = dto.getImporte();
        this.saldoPosterior = dto.getSaldoPosterior();
        this.lote = dto.getLote();
        this.centroCosto = dto.getCentroCosto();
        this.activo = dto.getActivo();
    }
}
