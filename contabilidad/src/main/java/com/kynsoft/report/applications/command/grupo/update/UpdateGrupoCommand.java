package com.kynsoft.report.applications.command.grupo.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateGrupoCommand implements ICommand {

    private UUID id;
    private String nombre;
    private String descripcion;
    private UUID jefeId;

    public UpdateGrupoCommand(UUID id, String nombre, String descripcion, UUID jefeId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.jefeId = jefeId;
    }

    public static UpdateGrupoCommand fromRequest(UpdateGrupoRequest request) {
        return new UpdateGrupoCommand(
                request.getId(),
                request.getNombre(),
                request.getDescripcion(),
                request.getJefeId()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateGrupoMessage(id);
    }
}
