package com.kynsoft.report.applications.query.grupo.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IGrupoService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SearchGrupoQueryHandler implements IQueryHandler<SearchGrupoQuery, PaginatedResponse> {

    private final IGrupoService serviceImpl;

    public SearchGrupoQueryHandler(IGrupoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    @Transactional("readTransactionManager")
    public PaginatedResponse handle(SearchGrupoQuery query) {
        return serviceImpl.search(query.getPageable(), query.getFilterCriteria());
    }
}
