package com.kynsoft.report.applications.command.planingresoventa.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UpdatePlanIngresoVentaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_PLAN_INGRESO_VENTA";
    public UpdatePlanIngresoVentaMessage(UUID id) { this.id = id; }
}
