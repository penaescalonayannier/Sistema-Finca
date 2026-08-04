package com.kynsoft.report.applications.command.cliente.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateClienteCommand implements ICommand {

    private UUID id;
    private String cuenta;
    private String nombre;
    private String ruc;
    private String direccion;

    public UpdateClienteCommand(UUID id, String cuenta, String nombre, String ruc, String direccion) {
        this.id = id;
        this.cuenta = cuenta;
        this.nombre = nombre;
        this.ruc = ruc;
        this.direccion = direccion;
    }

    public static UpdateClienteCommand fromRequest(UpdateClienteRequest request, UUID id) {
        return new UpdateClienteCommand(
                id,
                request.getCuenta(),
                request.getNombre(),
                request.getRuc(),
                request.getDireccion()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateClienteMessage(id);
    }
}
