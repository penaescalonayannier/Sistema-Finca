package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.dto.EstadoStock;
import com.kynsoft.report.domain.dto.UnidadMedida;
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
public class AlmacenFincaProductoResponse implements IResponse {
    private UUID id;
    private UUID almacenId;
    private String almacenNombre;
    private String almacenInventario;
    private UUID fincaProductoId;
    private UUID productoId;
    private String productoCode;
    private String productoName;
    private Double productoPrice;
    private UnidadMedida unidadMedida;
    private Double stock;
    private Double stockMinimo;
    private Double stockMaximo;
    private EstadoStock estadoStock;
    private Boolean activo;
    private Boolean alertaStockBajo;
    private Double deficit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AlmacenFincaProductoResponse(AlmacenFincaProductoDto dto) {
        this.id = dto.getId();
        this.almacenId = dto.getAlmacenId();
        this.almacenNombre = dto.getAlmacenNombre();
        this.almacenInventario = dto.getAlmacenInventario();
        this.fincaProductoId = dto.getFincaProductoId();
        this.productoId = dto.getProductoId();
        this.productoCode = dto.getProductoCode();
        this.productoName = dto.getProductoName();
        this.productoPrice = dto.getProductoPrice();
        this.unidadMedida = dto.getUnidadMedida();
        this.stock = dto.getStock();
        this.stockMinimo = dto.getStockMinimo();
        this.stockMaximo = dto.getStockMaximo();
        this.estadoStock = dto.getEstadoStock();
        this.activo = dto.getActivo();
        this.alertaStockBajo = dto.getAlertaStockBajo();
        this.deficit = dto.getDeficit();
        this.createdAt = dto.getCreatedAt();
        this.updatedAt = dto.getUpdatedAt();
    }
}
