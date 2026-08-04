package com.kynsoft.report.applications.command.trabajador.asignarGrupo;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AsignarGrupoTrabajadorCommand implements ICommand {

    private UUID trabajadorId;
    private UUID grupoId;

    public AsignarGrupoTrabajadorCommand(UUID trabajadorId, UUID grupoId) {
        this.trabajadorId = trabajadorId;
        this.grupoId = grupoId;
    }

    public static AsignarGrupoTrabajadorCommand fromRequest(AsignarGrupoTrabajadorRequest request) {
        return new AsignarGrupoTrabajadorCommand(
                request.getTrabajadorId(),
                request.getGrupoId()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new AsignarGrupoTrabajadorMessage(trabajadorId);
    }
}
