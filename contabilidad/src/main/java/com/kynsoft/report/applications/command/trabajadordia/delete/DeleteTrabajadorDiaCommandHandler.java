package com.kynsoft.report.applications.command.trabajadordia.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.ITrabajadorDiaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DeleteTrabajadorDiaCommandHandler 
    implements ICommandHandler<DeleteTrabajadorDiaCommand> {

    private final ITrabajadorDiaService trabajadorDiaService;

    @Override
    public void handle(DeleteTrabajadorDiaCommand command) {
        trabajadorDiaService.delete(command.getId());
    }
}