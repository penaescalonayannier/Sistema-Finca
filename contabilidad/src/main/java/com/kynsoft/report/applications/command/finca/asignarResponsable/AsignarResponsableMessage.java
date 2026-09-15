package com.kynsoft.report.applications.command.finca.asignarResponsable;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AsignarResponsableMessage implements ICommandMessage {
    private UUID id;
    private UUID responsableId;
}
