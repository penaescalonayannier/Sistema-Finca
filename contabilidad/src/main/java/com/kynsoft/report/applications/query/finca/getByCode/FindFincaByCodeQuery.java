package com.kynsoft.report.applications.query.finca.getByCode;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindFincaByCodeQuery implements IQuery {
    private String code;
}
