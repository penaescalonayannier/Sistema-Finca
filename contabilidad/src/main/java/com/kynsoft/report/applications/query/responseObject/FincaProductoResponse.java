package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.dto.TipoProducto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FincaProductoResponse implements IResponse {
    private UUID id;
    private UUID fincaId;
    private String fincaCode;
    private String fincaName;
    private UUID productoId;
    private String productoCode;
    private String productoName;
    private Double productoPrice;
    private TipoProducto productoTipo;
    private Double stock;
    private Double stockMinimo;
    private Boolean alertaStockBajo;
    private Boolean activo;

    public FincaProductoResponse(FincaProductoDto dto) {
        this.id = dto.getId();
        this.fincaId = dto.getFincaId();
        this.fincaCode = dto.getFincaCode();
        this.fincaName = dto.getFincaName();
        this.productoId = dto.getProductoId();
        this.productoCode = dto.getProductoCode();
        this.productoName = dto.getProductoName();
        this.productoPrice = dto.getProductoPrice();
        this.productoTipo = dto.getProductoTipo();
        this.stock = dto.getStock();
        this.stockMinimo = dto.getStockMinimo();
        this.alertaStockBajo = dto.getAlertaStockBajo();
        this.activo = dto.getActivo();
    }
}
