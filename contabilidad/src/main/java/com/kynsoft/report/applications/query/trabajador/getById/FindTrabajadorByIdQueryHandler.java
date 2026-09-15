package com.kynsoft.report.applications.query.trabajador.getById;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.TrabajadorResponse;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.ITrabajadorService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FindTrabajadorByIdQueryHandler
    implements IQueryHandler<FindTrabajadorByIdQuery, TrabajadorResponse> {

    private final ITrabajadorService service;

    public FindTrabajadorByIdQueryHandler(ITrabajadorService service) {
        this.service = service;
    }

    @Override
    @Transactional("readTransactionManager")
    public TrabajadorResponse handle(FindTrabajadorByIdQuery query) {
        TrabajadorDto response = service.findById(query.getId());
        return new TrabajadorResponse(response);
    }
}