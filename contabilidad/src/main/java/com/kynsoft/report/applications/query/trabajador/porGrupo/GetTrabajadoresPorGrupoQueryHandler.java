package com.kynsoft.report.applications.query.trabajador.porGrupo;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.ITrabajadorService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetTrabajadoresPorGrupoQueryHandler implements IQueryHandler<GetTrabajadoresPorGrupoQuery, PaginatedResponse> {

    private final ITrabajadorService service;

    @Override
    public PaginatedResponse handle(GetTrabajadoresPorGrupoQuery query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getPageSize());
        return service.findByGrupoId(query.getGrupoId(), pageable);
    }
}
