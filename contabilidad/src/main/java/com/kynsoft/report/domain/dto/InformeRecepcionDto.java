package com.kynsoft.report.domain.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Builder
public class InformeRecepcionDto {
    private UUID id; private UUID fincaId; private UUID almacenId; private String numeroDocumento;
    private TipoMovimientoStock tipoFuente; private String numeroFuente; private LocalDate fechaDocumento;
    private String proveedor; private String responsableEntrega; private String responsableRecibe;
    private String observaciones; private String estado; private UUID movimientoStockId; private LocalDateTime createdAt;
    private List<Linea> lineas;
    @Getter @Builder public static class Linea {
        private UUID almacenFincaProductoId; private UUID fincaProductoId; private UUID productoId;
        private String productoCodigo; private String productoNombre; private String unidadMedida;
        private Double cantidad; private Double costoUnitario; private Double importe; private Double saldoPosterior;
    }
}
