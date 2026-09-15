package com.kynsoft.report.applications.query.almacen.search;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetSearchAlmacenQuery implements IQuery {
    private final Pageable pageable;
    private final List<FilterCriteria> filterCriteria;
    private final String query;
}
