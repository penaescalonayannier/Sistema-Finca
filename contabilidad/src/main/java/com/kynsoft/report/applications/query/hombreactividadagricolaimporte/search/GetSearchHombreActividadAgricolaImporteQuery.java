package com.kynsoft.report.applications.query.hombreactividadagricolaimporte.search;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.domain.request.FilterCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetSearchHombreActividadAgricolaImporteQuery implements IQuery {
    private Pageable pageable;
    private List<FilterCriteria> filter;
    private String query;
}