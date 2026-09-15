package com.kynsoft.report.applications.command.trabajador.transferir;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.TransferirTrabajadorResponse;
import com.kynsoft.report.domain.services.ITrabajadorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransferirTrabajadorCommandHandler implements ICommandHandler<TransferirTrabajadorCommand> {

    private final ITrabajadorService serviceImpl;

    @Override
    public void handle(TransferirTrabajadorCommand command) {
        TransferirTrabajadorResponse response = serviceImpl.transferir(
                command.getTrabajadorId(),
                command.getNuevaFincaId()
        );
        command.setFincaAnterior(response.getFincaAnterior());
        command.setFincaNueva(response.getFincaNueva());
    }
}
