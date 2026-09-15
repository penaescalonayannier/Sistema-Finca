package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.AlmacenDto;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlmacenResponse implements IResponse {
    private UUID id;
    private String nombre;
    private String descripcion;
    private String inventario;
    private Boolean esPrincipal;
    private Boolean activo;
    private UUID fincaId;
    private String fincaCode;
    private String fincaName;
    private List<FincaProductoResponse> productos;
    private Integer productosCount;
    private Long cantidadProductos;
    private Long stockTotal;

    public AlmacenResponse(AlmacenDto dto) {
        this.id = dto.getId();
        this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion();
        this.inventario = dto.getInventario();
        this.esPrincipal = dto.getEsPrincipal();
        this.activo = dto.getActivo();
        this.fincaId = dto.getFincaId();
        this.fincaCode = dto.getFincaCode();
        this.fincaName = dto.getFincaName();
        this.cantidadProductos = dto.getCantidadProductos();
        this.stockTotal = dto.getStockTotal();
        if (dto.getProductos() != null) {
            this.productos = dto.getProductos().stream()
                .map(FincaProductoResponse::new)
                .collect(Collectors.toList());
            this.productosCount = dto.getProductos().size();
        } else {
            this.productosCount = 0;
        }
    }

    public AlmacenResponse(AlmacenDto dto, int productCount) {
        this.id = dto.getId();
        this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion();
        this.inventario = dto.getInventario();
        this.esPrincipal = dto.getEsPrincipal();
        this.activo = dto.getActivo();
        this.fincaId = dto.getFincaId();
        this.fincaCode = dto.getFincaCode();
        this.fincaName = dto.getFincaName();
        this.productosCount = productCount;
        this.cantidadProductos = dto.getCantidadProductos();
        this.stockTotal = dto.getStockTotal();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FincaProductoResponse {
        private UUID id;
        private UUID fincaProductoId; // ID real del FincaProducto para operaciones de salida
        private String fincaCode;
        private String fincaName;
        private String productoCode;
        private String productoName;
        private Double productoPrice;
        private Double stock;

        public FincaProductoResponse(FincaProductoDto dto) {
            this.id = dto.getId();
            this.fincaProductoId = dto.getFincaProductoId();
            this.fincaCode = dto.getFincaCode();
            this.fincaName = dto.getFincaName();
            this.productoCode = dto.getProductoCode();
            this.productoName = dto.getProductoName();
            this.productoPrice = dto.getProductoPrice();
            this.stock = dto.getStock();
        }
    }
}
