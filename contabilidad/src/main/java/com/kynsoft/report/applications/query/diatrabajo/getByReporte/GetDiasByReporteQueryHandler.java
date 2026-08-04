package com.kynsoft.report.applications.query.diatrabajo.getByReporte;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.DiaTrabajoListResponse;
import com.kynsoft.report.applications.query.responseObject.DiaTrabajoResponse;
import com.kynsoft.report.applications.query.responseObject.TrabajadorDiaResponse;
import com.kynsoft.report.domain.dto.DiaTrabajoDto;
import com.kynsoft.report.domain.services.IDiaTrabajoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GetDiasByReporteQueryHandler 
    implements IQueryHandler<GetDiasByReporteQuery, DiaTrabajoListResponse> {

    private final IDiaTrabajoService diaTrabajoService;

    @Override
    public DiaTrabajoListResponse handle(GetDiasByReporteQuery query) {
        // Usar el método que trae los trabajadores
        List<DiaTrabajoDto> dias = diaTrabajoService.findByReporteIdWithTrabajadores(query.getReporteId());
        
        List<DiaTrabajoResponse> responses = dias.stream()
                .map(dia -> {
                    // Convertir trabajadores a response
                    List<TrabajadorDiaResponse> trabajadoresResponse = null;
                    if (dia.getTrabajadores() != null) {
                        trabajadoresResponse = dia.getTrabajadores().stream()
                                .map(TrabajadorDiaResponse::new)
                                .collect(Collectors.toList());
                    }
                    
                    return new DiaTrabajoResponse(
                        dia.getId(),
                        dia.getFecha(),
                        dia.getReporteId(),
                        trabajadoresResponse
                    );
                })
                .collect(Collectors.toList());
        
        return new DiaTrabajoListResponse(
            responses,
            (long) responses.size(),
            1,
            0,
            responses.size()
        );
    }
}