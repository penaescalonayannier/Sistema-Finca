package com.kynsoft.report.applications.command.diatrabajo.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IDiaTrabajoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DeleteDiaTrabajoCommandHandler 
    implements ICommandHandler<DeleteDiaTrabajoCommand> {

    private final IDiaTrabajoService diaTrabajoService;

    @Override
    public void handle(DeleteDiaTrabajoCommand command) {
        diaTrabajoService.delete(command.getId());
    }
}