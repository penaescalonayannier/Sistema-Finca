package com.kynsoft.report.applications.command.finca.create;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateFincaCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private String direccion;
    private String telefono;
    private Double area;

    public static CreateFincaCommand fromRequest(CreateFincaRequest request) {
        return new CreateFincaCommand(
                UUID.randomUUID(),
                request.getCode(),
                request.getName(),
                request.getDescription(),
                request.getDireccion(),
                request.getTelefono(),
                request.getArea()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateFincaMessage(id);
    }
}