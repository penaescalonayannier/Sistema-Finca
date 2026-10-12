package com.kynsoft.report.applications.command.planingresoventa.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;
import lombok.Getter;

@Getter
public class DeletePlanIngresoVentaMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_PLAN_INGRESO_VENTA";
    public DeletePlanIngresoVentaMessage(UUID id) { this.id = id; }
}
