package com.kynsoft.report.applications.query;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.applications.query.responseObject.EvaluacionResponse;
import com.kynsoft.report.domain.dto.EvaluacionDto;
import com.kynsoft.report.domain.services.IEvaluacionService;
import org.springframework.stereotype.Component;

@Component
public class GetEvaluacionQueryHandler implements IQueryHandler<GetEvaluacionQuery, EvaluacionResponse> {

    private final IEvaluacionService serviceImpl;

    public GetEvaluacionQueryHandler(IEvaluacionService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public EvaluacionResponse handle(GetEvaluacionQuery query) {
        EvaluacionDto dto = serviceImpl.findById(query.getId());
        return new EvaluacionResponse(dto);
    }
}
