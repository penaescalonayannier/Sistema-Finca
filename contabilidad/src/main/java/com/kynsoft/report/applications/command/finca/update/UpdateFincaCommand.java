package com.kynsoft.report.applications.command.finca.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateFincaCommand implements ICommand {
    private UUID id;
    private String name;
    private String description;
    private String direccion;
    private String telefono;
    private UUID responsableId;
    private Double area;

    public static UpdateFincaCommand fromRequest(UpdateFincaRequest request, UUID id) {
        return new UpdateFincaCommand(
                id,
                request.getName(),
                request.getDescription(),
                request.getDireccion(),
                request.getTelefono(),
                request.getResponsableId(),
                request.getArea()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateFincaMessage(id);
    }
}