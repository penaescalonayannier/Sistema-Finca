package com.kynsoft.report.applications.query.report.estadoCuenta.export;

import com.kynsof.share.core.domain.bus.query.IQuery;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class GetExportEstadoCuentaQuery implements IQuery {

    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    
    public GetExportEstadoCuentaQuery(LocalDate fechaInicio, LocalDate fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
}