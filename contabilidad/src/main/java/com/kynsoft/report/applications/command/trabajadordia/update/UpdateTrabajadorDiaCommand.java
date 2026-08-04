package com.kynsoft.report.applications.command.trabajadordia.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateTrabajadorDiaCommand implements ICommand {
    private UUID id;
    private String horas;
    private String norma;

    public static UpdateTrabajadorDiaCommand fromRequest(UpdateTrabajadorDiaRequest request, UUID id) {
        return new UpdateTrabajadorDiaCommand(
                id,
                request.getHoras(),
                request.getNorma()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateTrabajadorDiaMessage(id);
    }
}