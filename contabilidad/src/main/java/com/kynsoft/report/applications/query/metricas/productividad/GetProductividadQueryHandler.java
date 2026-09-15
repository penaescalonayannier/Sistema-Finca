package com.kynsoft.report.applications.query.metricas.productividad;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.ProductividadListResponse;
import com.kynsoft.report.domain.services.IReportesMetricasService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetProductividadQueryHandler implements IQueryHandler<GetProductividadQuery, ProductividadListResponse> {

    private final IReportesMetricasService reportesMetricasService;

    @Override
    public ProductividadListResponse handle(GetProductividadQuery query) {
        var items = reportesMetricasService.calcularProductividad(query.getYear(), query.getMes(), query.getTrabajadorId());
        return new ProductividadListResponse(items);
    }
}
