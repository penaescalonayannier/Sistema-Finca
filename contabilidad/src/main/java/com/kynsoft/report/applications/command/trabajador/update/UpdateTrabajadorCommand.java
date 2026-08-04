package com.kynsoft.report.applications.command.trabajador.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateTrabajadorCommand implements ICommand {

    private final UUID id;
    private final String ruc;
    private final String nombre;
    private final String cuenta;
    private final Boolean activo;

    public UpdateTrabajadorCommand(UUID id, String ruc, String nombre, String cuenta, Boolean activo) {
        this.id = id;
        this.ruc = ruc;
        this.nombre = nombre;
        this.cuenta = cuenta;
        this.activo = activo;
    }

    public static UpdateTrabajadorCommand fromRequest(UpdateTrabajadorRequest request, UUID id) {
        return new UpdateTrabajadorCommand(
                id,
                request.getRuc(),
                request.getNombre(),
                request.getCuenta(),
                request.getActivo()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateTrabajadorMessage(id);
    }
}