package com.kynsoft.report.applications.command.report.estadoCuenta.createBatch;

import com.kynsoft.report.applications.command.report.estadoCuenta.create.CreateEstadoCuentaRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateEstadoCuentaBatchRequest {

    private List<CreateEstadoCuentaRequest> operaciones;
}
