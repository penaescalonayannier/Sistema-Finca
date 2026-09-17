package com.kynsoft.report.applications.command.trabajador.create;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateTrabajadorCommand implements ICommand {

    private UUID id;
    private String ruc;
    private String nombre;
    private String cuenta;
    private UUID fincaId;
    private UUID grupoId;
    private UUID cargoId;
    private UUID plazaId;

    public static CreateTrabajadorCommand fromRequest(CreateTrabajadorRequest request) {
        return new CreateTrabajadorCommand(
                UUID.randomUUID(),
                request.getRuc(),
                request.getNombre(),
                request.getCuenta(),
                request.getFincaId(),
                request.getGrupoId(),
                request.getCargoId(),
                request.getPlazaId()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateTrabajadorMessage(id);
    }
}
