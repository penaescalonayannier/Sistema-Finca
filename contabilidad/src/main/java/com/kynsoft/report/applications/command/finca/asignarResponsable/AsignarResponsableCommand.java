package com.kynsoft.report.applications.command.finca.asignarResponsable;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class AsignarResponsableCommand implements ICommand {
    private UUID fincaId;
    private UUID responsableId;

    public static AsignarResponsableCommand fromRequest(AsignarResponsableRequest request, UUID fincaId) {
        return new AsignarResponsableCommand(fincaId, request.getResponsableId());
    }

    @Override
    public ICommandMessage getMessage() {
        return new AsignarResponsableMessage(fincaId, responsableId);
    }
}
