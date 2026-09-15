package com.kynsoft.report.applications.query.almacen.porfinca;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetAlmacenesPorFincaQuery implements IQuery {
    private UUID fincaId;
    private Pageable pageable;
}
