package com.kynsoft.report.applications.query.planingresoventa.search;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
public class GetSearchPlanIngresoVentaQuery implements IQuery {
    private final Pageable pageable;
    private final List<FilterCriteria> filter;
    private final String query;
    public GetSearchPlanIngresoVentaQuery(Pageable pageable, List<FilterCriteria> filter, String query) {
        this.pageable = pageable; this.filter = filter; this.query = query;
    }
}
