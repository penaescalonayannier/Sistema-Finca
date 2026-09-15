package com.kynsoft.report.applications.query.report.estadoCuenta.getByDateRange;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import java.time.LocalDate;
import lombok.Getter;

@Getter
// El query retornará una lista paginada o una estructura de respuesta (ajuste el tipo si usa paginación)
public class GetEstadoCuentaByDateQuery implements IQuery { 

    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    
    public GetEstadoCuentaByDateQuery(LocalDate fechaInicio, LocalDate fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
}