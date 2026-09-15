package com.kynsoft.report.applications.command.trabajador.transferir;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class TransferirTrabajadorMessage implements ICommandMessage {
    private UUID id;
    private String fincaAnterior;
    private String fincaNueva;
}
