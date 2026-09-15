package com.kynsoft.report.applications.command.trabajador.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateTrabajadorCommand implements ICommand {

    private UUID id;
    // RUC no se puede modificar (RN-09)
    private String nombre;
    private String cuenta;
    private UUID fincaId;
    private UUID grupoId;
    private UUID cargoId;
    private Boolean activo;

    public static UpdateTrabajadorCommand fromRequest(UpdateTrabajadorRequest request, UUID id) {
        return new UpdateTrabajadorCommand(
                id,
                request.getNombre(),
                request.getCuenta(),
                request.getFincaId(),
                request.getGrupoId(),
                request.getCargoId(),
                request.getActivo()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateTrabajadorMessage(id);
    }
}