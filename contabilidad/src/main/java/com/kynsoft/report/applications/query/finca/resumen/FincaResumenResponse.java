package com.kynsoft.report.applications.query.finca.resumen;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.FincaResumenDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FincaResumenResponse implements IResponse {
    private UUID id;
    private String code;
    private String name;
    private Double area;
    private String responsableNombre;
    private EstadisticasResponse estadisticas;

    public FincaResumenResponse(FincaResumenDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.area = dto.getArea();
        this.responsableNombre = dto.getResponsableNombre();
        if (dto.getEstadisticas() != null) {
            this.estadisticas = new EstadisticasResponse(dto.getEstadisticas());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstadisticasResponse {
        private Long totalTrabajadores;
        private Long totalProductos;
        private Long totalProduccionMes;
        private Long totalSalidasMes;
        private Double stockTotalValorizado;

        public EstadisticasResponse(FincaResumenDto.FincaEstadisticasDto dto) {
            this.totalTrabajadores = dto.getTotalTrabajadores();
            this.totalProductos = dto.getTotalProductos();
            this.totalProduccionMes = dto.getTotalProduccionMes();
            this.totalSalidasMes = dto.getTotalSalidasMes();
            this.stockTotalValorizado = dto.getStockTotalValorizado();
        }
    }
}
