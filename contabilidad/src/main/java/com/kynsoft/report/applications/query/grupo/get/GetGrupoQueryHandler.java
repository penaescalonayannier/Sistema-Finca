package com.kynsoft.report.applications.query.grupo.get;

import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.GrupoResponse;
import com.kynsoft.report.domain.dto.GrupoDto;
import com.kynsoft.report.domain.services.IGrupoService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetGrupoQueryHandler implements IQueryHandler<GetGrupoQuery, GrupoResponse> {

    private final IGrupoService serviceImpl;

    public GetGrupoQueryHandler(IGrupoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    @Transactional("readTransactionManager")
    public GrupoResponse handle(GetGrupoQuery query) {
        GrupoDto dto = serviceImpl.findById(query.getId());
        return new GrupoResponse(dto);
    }
}
