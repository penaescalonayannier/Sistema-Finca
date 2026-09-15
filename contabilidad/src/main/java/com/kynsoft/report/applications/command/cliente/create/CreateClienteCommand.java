package com.kynsoft.report.applications.command.cliente.create;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateClienteCommand implements ICommand {

    private UUID id;
    private String cuenta;
    private String nombre;
    private String ruc;
    private String direccion;

    public CreateClienteCommand(String cuenta, String nombre, String ruc, String direccion) {
        this.id = UUID.randomUUID();
        this.cuenta = cuenta;
        this.nombre = nombre;
        this.ruc = ruc;
        this.direccion = direccion;
    }

    public static CreateClienteCommand fromRequest(CreateClienteRequest request) {
        return new CreateClienteCommand(
                request.getCuenta(),
                request.getNombre(),
                request.getRuc(),
                request.getDireccion()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateClienteMessage(id);
    }
}
