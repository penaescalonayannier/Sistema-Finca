package com.kynsoft.report.applications.command.almacen.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateAlmacenCommand implements ICommand {
    private UUID id;
    private String nombre;
    private String inventario;
    private UUID fincaId;

    public static UpdateAlmacenCommand fromRequest(UpdateAlmacenRequest request, UUID id) {
        return new UpdateAlmacenCommand(
                id,
                request.getNombre(),
                request.getInventario(),
                request.getFincaId()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateAlmacenMessage(id);
    }
}
