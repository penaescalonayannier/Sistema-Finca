package com.kynsoft.report.applications.query.fincaproducto.alertas;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@AllArgsConstructor
public class GetAlertasStockBajoQuery implements IQuery {
    private Pageable pageable;
}
