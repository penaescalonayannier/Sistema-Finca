package com.kynsoft.report.applications.query.report.cuenta110.search;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetSearchCuenta110Query implements IQuery {

    private Pageable pageable;
    private List<FilterCriteria> filter;
    private String query;
}
