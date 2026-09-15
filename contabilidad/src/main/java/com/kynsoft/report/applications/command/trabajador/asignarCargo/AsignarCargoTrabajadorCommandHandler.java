package com.kynsoft.report.applications.command.trabajador.asignarCargo;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.ITrabajadorService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AsignarCargoTrabajadorCommandHandler implements ICommandHandler<AsignarCargoTrabajadorCommand> {

    private final ITrabajadorService serviceImpl;

    public AsignarCargoTrabajadorCommandHandler(ITrabajadorService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    @Transactional("writeTransactionManager")
    public void handle(AsignarCargoTrabajadorCommand command) {
        TrabajadorDto dto = serviceImpl.findById(command.getTrabajadorId());
        dto.setCargoId(command.getCargoId());
        serviceImpl.update(dto);
    }
}
