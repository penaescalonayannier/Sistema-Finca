package com.kynsoft.report.applications.query.fincaproducto.getbyid;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.FincaProductoResponse;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.services.IFincaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetFincaProductoByIdQueryHandler implements IQueryHandler<GetFincaProductoByIdQuery, FincaProductoResponse> {

    private final IFincaProductoService service;

    @Override
    public FincaProductoResponse handle(GetFincaProductoByIdQuery query) {
        FincaProductoDto dto = service.getById(query.getId());
        return new FincaProductoResponse(dto);
    }
}
