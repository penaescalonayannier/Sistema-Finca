package com.kynsoft.report.applications.command.trabajador.reactivar;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReactivarTrabajadorMessage implements ICommandMessage {
    private UUID id;
}
