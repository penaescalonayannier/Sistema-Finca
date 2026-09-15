package com.kynsoft.report.applications.query.produccionterminada.getall;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
public class GetAllProduccionTerminadaQuery implements IQuery {
    private final Pageable pageable;
    private final List<FilterCriteria> filter;
    private final String query;

    public GetAllProduccionTerminadaQuery(Pageable pageable, List<FilterCriteria> filter, String query) {
        this.pageable = pageable;
        this.filter = filter;
        this.query = query;
    }
}
