package com.kynsoft.report.applications.query;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Setter
public class SearchEvaluacionQuery implements IQuery {
    private Pageable pageable;
    private List<FilterCriteria> filterCriteria;

    public SearchEvaluacionQuery(Pageable pageable, List<FilterCriteria> filterCriteria) {
        this.pageable = pageable;
        this.filterCriteria = filterCriteria;
    }
}
