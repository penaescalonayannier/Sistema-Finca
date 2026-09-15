package com.kynsoft.report.applications.query.finca.resumen;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.domain.dto.FincaResumenDto;
import com.kynsoft.report.domain.services.IFincaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetFincaResumenQueryHandler implements IQueryHandler<GetFincaResumenQuery, FincaResumenResponse> {

    private final IFincaService service;

    @Override
    public FincaResumenResponse handle(GetFincaResumenQuery query) {
        FincaResumenDto dto = service.getResumen(query.getId());
        return new FincaResumenResponse(dto);
    }
}
