package com.kynsoft.report.applications.query.fincaproducto.getproductos;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.FincaProductoListResponse;
import com.kynsoft.report.applications.query.responseObject.FincaProductoResponse;
import com.kynsoft.report.domain.dto.FincaProductoDto;
import com.kynsoft.report.domain.services.IFincaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GetProductosDeFincaQueryHandler
        implements IQueryHandler<GetProductosDeFincaQuery, FincaProductoListResponse> {

    private final IFincaProductoService service;

    @Override
    public FincaProductoListResponse handle(GetProductosDeFincaQuery query) {
        List<FincaProductoDto> dtos = service.obtenerProductosDeFinca(query.getFincaId());
        List<FincaProductoResponse> responses = dtos.stream()
                .map(FincaProductoResponse::new)
                .collect(Collectors.toList());

        return new FincaProductoListResponse(responses);
    }
}
