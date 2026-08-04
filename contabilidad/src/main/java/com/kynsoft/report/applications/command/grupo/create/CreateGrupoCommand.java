package com.kynsoft.report.applications.command.grupo.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateGrupoCommand implements ICommand {

    private UUID id;
    private String nombre;
    private String descripcion;
    private UUID jefeId;

    public CreateGrupoCommand(String nombre, String descripcion, UUID jefeId) {
        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.jefeId = jefeId;
    }

    public static CreateGrupoCommand fromRequest(CreateGrupoRequest request) {
        return new CreateGrupoCommand(
                request.getNombre(),
                request.getDescripcion(),
                request.getJefeId()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateGrupoMessage(id);
    }
}
