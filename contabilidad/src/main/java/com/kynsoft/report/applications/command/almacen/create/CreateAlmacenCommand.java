package com.kynsoft.report.applications.command.almacen.create;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateAlmacenCommand implements ICommand {
    private UUID id;
    private String nombre;
    private String descripcion;
    private UUID fincaId;
    private String inventario; // Set by handler after creation

    public CreateAlmacenCommand(String nombre, String descripcion, UUID fincaId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fincaId = fincaId;
    }

    public static CreateAlmacenCommand fromRequest(CreateAlmacenRequest request) {
        return new CreateAlmacenCommand(
                request.getNombre(),
                request.getDescripcion(),
                request.getFincaId()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateAlmacenMessage(id, inventario);
    }
}
