package com.kynsoft.report.applications.query.deudaTrabajador.search;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
public class GetSearchDeudaTrabajadorQuery implements IQuery {

    private final Pageable pageable;
    private final List<FilterCriteria> filterCriteria;

    public GetSearchDeudaTrabajadorQuery(Pageable pageable, List<FilterCriteria> filterCriteria) {
        this.pageable = pageable;
        this.filterCriteria = filterCriteria;
    }
}
