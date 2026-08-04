package com.kynsoft.report.applications.command.trabajador.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateTrabajadorCommand implements ICommand {

    private UUID id;
    private String ruc;
    private String nombre;
    private String cuenta;
    private Boolean activo; // Activo/Inactivo

    public CreateTrabajadorCommand(String ruc, String nombre, String cuenta, Boolean activo) {
        this.id = UUID.randomUUID();
        this.ruc = ruc;
        this.nombre = nombre;
        this.cuenta = cuenta;
        this.activo = activo != null ? activo : true;
    }

    public static CreateTrabajadorCommand fromRequest(CreateTrabajadorRequest request) {
        return new CreateTrabajadorCommand(
                request.getRuc(),
                request.getNombre(),
                request.getCuenta(),
                request.getActivo()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateTrabajadorMessage(id);
    }
}
