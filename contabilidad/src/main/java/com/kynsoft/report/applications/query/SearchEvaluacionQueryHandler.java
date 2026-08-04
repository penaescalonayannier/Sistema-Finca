package com.kynsoft.report.applications.query;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IEvaluacionService;
import org.springframework.stereotype.Component;

@Component
public class SearchEvaluacionQueryHandler implements IQueryHandler<SearchEvaluacionQuery, PaginatedResponse> {

    private final IEvaluacionService serviceImpl;

    public SearchEvaluacionQueryHandler(IEvaluacionService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public PaginatedResponse handle(SearchEvaluacionQuery query) {
        return serviceImpl.search(query.getPageable(), query.getFilterCriteria());
    }
}
