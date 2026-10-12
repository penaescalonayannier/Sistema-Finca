package com.kynsoft.report.applications.query.planingresoventa.getbyid;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import java.util.UUID;
import lombok.Getter;

@Getter
public class FindPlanIngresoVentaByIdQuery implements IQuery {
    private final UUID id;
    public FindPlanIngresoVentaByIdQuery(UUID id) { this.id = id; }
}
