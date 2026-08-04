package com.kynsoft.report.applications.query.trabajador.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.ITrabajadorService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetSearchTrabajadorQueryHandler
    implements IQueryHandler<GetSearchTrabajadorQuery, PaginatedResponse> {

    private final ITrabajadorService service;

    public GetSearchTrabajadorQueryHandler(ITrabajadorService service) {
        this.service = service;
    }

    @Override
    @Transactional("readTransactionManager")
    public PaginatedResponse handle(GetSearchTrabajadorQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}