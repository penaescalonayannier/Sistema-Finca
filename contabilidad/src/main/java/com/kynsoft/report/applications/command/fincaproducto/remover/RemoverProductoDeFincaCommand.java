package com.kynsoft.report.applications.command.fincaproducto.remover;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class RemoverProductoDeFincaCommand implements ICommand {
    private UUID fincaId;
    private UUID productoId;

    public static RemoverProductoDeFincaCommand fromRequest(RemoverProductoDeFincaRequest request) {
        return new RemoverProductoDeFincaCommand(
                request.getFincaId(),
                request.getProductoId()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new RemoverProductoDeFincaMessage(fincaId, productoId);
    }
}