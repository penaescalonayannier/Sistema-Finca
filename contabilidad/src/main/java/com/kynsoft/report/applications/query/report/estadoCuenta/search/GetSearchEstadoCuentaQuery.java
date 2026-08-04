package com.kynsoft.report.applications.query.report.estadoCuenta.search;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.domain.request.FilterCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetSearchEstadoCuentaQuery implements IQuery {

    private Pageable pageable;
    private List<FilterCriteria> filter;
    private String query;
}
