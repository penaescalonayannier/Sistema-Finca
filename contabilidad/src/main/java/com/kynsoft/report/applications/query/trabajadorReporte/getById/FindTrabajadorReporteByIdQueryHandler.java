package com.kynsoft.report.applications.query.trabajadorReporte.getById;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.TrabajadorReporteResponse;
import com.kynsoft.report.domain.dto.TrabajadorReporteDto;
import com.kynsoft.report.domain.services.ITrabajadorReporteService;
import org.springframework.stereotype.Component;

@Component
public class FindTrabajadorReporteByIdQueryHandler
        implements IQueryHandler<FindTrabajadorReporteByIdQuery, TrabajadorReporteResponse> {

    private final ITrabajadorReporteService reportService;

    public FindTrabajadorReporteByIdQueryHandler(ITrabajadorReporteService reportService) {
        this.reportService = reportService;
    }

    @Override
    public TrabajadorReporteResponse handle(FindTrabajadorReporteByIdQuery query) {
        TrabajadorReporteDto response = reportService.findById(query.getId());
        return new TrabajadorReporteResponse(response);
    }
}
