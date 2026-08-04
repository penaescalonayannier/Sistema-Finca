package com.kynsoft.report.applications.query.trabajadordia.getByDia;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.TrabajadorDiaListResponse;
import com.kynsoft.report.applications.query.responseObject.TrabajadorDiaResponse;
import com.kynsoft.report.domain.dto.TrabajadorDiaDto;
import com.kynsoft.report.domain.services.ITrabajadorDiaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GetTrabajadoresByDiaQueryHandler 
    implements IQueryHandler<GetTrabajadoresByDiaQuery, TrabajadorDiaListResponse> {

    private final ITrabajadorDiaService trabajadorDiaService;

    @Override
    public TrabajadorDiaListResponse handle(GetTrabajadoresByDiaQuery query) {
        List<TrabajadorDiaDto> trabajadores = trabajadorDiaService.findByDiaTrabajoId(query.getDiaTrabajoId());
        List<TrabajadorDiaResponse> responses = trabajadores.stream()
                .map(TrabajadorDiaResponse::new)
                .collect(Collectors.toList());
        
        return new TrabajadorDiaListResponse(
            responses,
            (long) responses.size(),
            1,
            0,
            responses.size()
        );
    }
}