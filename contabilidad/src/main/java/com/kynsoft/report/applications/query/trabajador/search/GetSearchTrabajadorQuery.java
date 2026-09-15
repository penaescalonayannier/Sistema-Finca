package com.kynsoft.report.applications.query.trabajador.search;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetSearchTrabajadorQuery implements IQuery {
    private Pageable pageable;
    private List<FilterCriteria> filter;
    private String query;
}