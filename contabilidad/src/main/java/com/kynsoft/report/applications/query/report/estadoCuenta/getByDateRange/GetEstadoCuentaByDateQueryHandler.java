package com.kynsoft.report.applications.query.report.estadoCuenta.getByDateRange;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.EstadoCuentaByDateRangeResponse;
import com.kynsoft.report.applications.query.responseObject.EstadoCuentaResponse;
import com.kynsoft.report.domain.dto.EstadoCuentaDto;
import com.kynsoft.report.domain.services.IEstadoCuentaService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetEstadoCuentaByDateQueryHandler implements IQueryHandler<GetEstadoCuentaByDateQuery, EstadoCuentaByDateRangeResponse> {

    private final IEstadoCuentaService estadoCuentaService;

    public GetEstadoCuentaByDateQueryHandler(IEstadoCuentaService estadoCuentaService) {
        this.estadoCuentaService = estadoCuentaService;
    }

    @Override
    public EstadoCuentaByDateRangeResponse handle(GetEstadoCuentaByDateQuery query) {
        
        // 1. Llamar al nuevo método del servicio que filtra solo por fechas
        List<EstadoCuentaDto> dtos = estadoCuentaService.findAllByDate(
            query.getFechaInicio(), 
            query.getFechaFin()
        );

        return EstadoCuentaByDateRangeResponse
                .builder()
                .response(dtos.stream()
                .map(this::toResponse) // Método de mapeo a la clase de respuesta
                .collect(Collectors.toList()))
                .build();
    }
    
    // Método de mapeo DTO -> Response (Ajuste esto a su lógica de mapeo real)
    private EstadoCuentaResponse toResponse(EstadoCuentaDto dto) {
        return EstadoCuentaResponse
                .builder()
                .id(dto.getId())
                .fecha(dto.getFecha())
                .importe(dto.getImporte())
                .observaciones(dto.getObservaciones())
                .refCorriente(dto.getRefCorriente())
                .refOrigen(dto.getRefOrigen())
                .tipo(dto.getTipo())
                .build();
    }
}