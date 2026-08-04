package com.kynsoft.report.applications.query.cargo.search;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.domain.request.FilterCriteria;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Setter
public class SearchCargoQuery implements IQuery {
    private Pageable pageable;
    private List<FilterCriteria> filterCriteria;

    public SearchCargoQuery(Pageable pageable, List<FilterCriteria> filterCriteria) {
        this.pageable = pageable;
        this.filterCriteria = filterCriteria;
    }
}
