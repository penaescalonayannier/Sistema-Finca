package com.kynsoft.report.applications.query.instrumentoTrabajo.getById;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.InstrumentoTrabajoResponse;
import com.kynsoft.report.domain.dto.InstrumentoTrabajoDto;
import com.kynsoft.report.domain.services.IInstrumentoTrabajoService;
import org.springframework.stereotype.Component;

@Component
public class FindInstrumentoTrabajoByIdQueryHandler 
    implements IQueryHandler<FindInstrumentoTrabajoByIdQuery, InstrumentoTrabajoResponse> {

    private final IInstrumentoTrabajoService service;

    public FindInstrumentoTrabajoByIdQueryHandler(IInstrumentoTrabajoService service) {
        this.service = service;
    }

    @Override
    public InstrumentoTrabajoResponse handle(FindInstrumentoTrabajoByIdQuery query) {
        InstrumentoTrabajoDto response = service.findById(query.getId());
        return new InstrumentoTrabajoResponse(response);
    }
}