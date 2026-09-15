package com.kynsoft.report.applications.query.finca.getByCode;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.FincaResponse;
import com.kynsoft.report.domain.dto.FincaDto;
import com.kynsoft.report.domain.services.IFincaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FindFincaByCodeQueryHandler implements IQueryHandler<FindFincaByCodeQuery, FincaResponse> {

    private final IFincaService service;

    @Override
    public FincaResponse handle(FindFincaByCodeQuery query) {
        FincaDto dto = service.findByCode(query.getCode());
        return new FincaResponse(dto);
    }
}
