package com.kynsoft.report.applications.query.trabajadorReporte.search;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.domain.request.FilterCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetSearchTrabajadorReporteQuery implements IQuery {
    private final Pageable pageable;
    private final List<FilterCriteria> filter;
    private final String query;
}