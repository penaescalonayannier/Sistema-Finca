package com.kynsoft.report.applications.command.trabajador.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.DeleteTrabajadorResponse;
import com.kynsoft.report.domain.services.ITrabajadorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DeleteTrabajadorCommandHandler implements ICommandHandler<DeleteTrabajadorCommand> {

    private final ITrabajadorService serviceImpl;

    @Override
    public void handle(DeleteTrabajadorCommand command) {
        DeleteTrabajadorResponse response = serviceImpl.delete(command.getId());
        command.setAdvertencias(response.getAdvertencias());
    }
}