package com.kynsoft.report.applications.query.grupo.search;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Setter
public class SearchGrupoQuery implements IQuery {
    private Pageable pageable;
    private List<FilterCriteria> filterCriteria;

    public SearchGrupoQuery(Pageable pageable, List<FilterCriteria> filterCriteria) {
        this.pageable = pageable;
        this.filterCriteria = filterCriteria;
    }
}
