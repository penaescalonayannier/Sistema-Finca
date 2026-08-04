package com.kynsoft.report.applications.query.cargo.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.ICargoService;
import org.springframework.stereotype.Component;

@Component
public class SearchCargoQueryHandler implements IQueryHandler<SearchCargoQuery, PaginatedResponse> {

    private final ICargoService serviceImpl;

    public SearchCargoQueryHandler(ICargoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public PaginatedResponse handle(SearchCargoQuery query) {
        return serviceImpl.search(query.getPageable(), query.getFilterCriteria());
    }
}
