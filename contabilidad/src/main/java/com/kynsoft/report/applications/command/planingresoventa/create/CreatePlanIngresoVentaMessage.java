package com.kynsoft.report.applications.command.planingresoventa.create;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;
import lombok.Getter;

@Getter
public class CreatePlanIngresoVentaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_PLAN_INGRESO_VENTA";
    public CreatePlanIngresoVentaMessage(UUID id) { this.id = id; }
}
