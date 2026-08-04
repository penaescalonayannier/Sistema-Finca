package com.kynsoft.report.applications.command.report.estadoCuenta.createBatch;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.applications.command.report.estadoCuenta.create.CreateEstadoCuentaRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateEstadoCuentaBatchCommand implements ICommand {

    private List<CreateEstadoCuentaRequest> operaciones;
    private List<UUID> createdIds;

    public CreateEstadoCuentaBatchCommand(List<CreateEstadoCuentaRequest> operaciones) {
        this.operaciones = operaciones;
        this.createdIds = new ArrayList<>();
    }

    public static CreateEstadoCuentaBatchCommand fromRequest(CreateEstadoCuentaBatchRequest request) {
        return new CreateEstadoCuentaBatchCommand(request.getOperaciones());
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateEstadoCuentaBatchMessage(createdIds);
    }
}
