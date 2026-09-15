package com.kynsoft.report.applications.command.salida.update;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdateSalidaMessage implements ICommandMessage {
    private UUID id;
}
