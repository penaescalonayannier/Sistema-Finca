package com.kynsoft.report.applications.command.trabajador.asignarGrupo;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.ITrabajadorService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AsignarGrupoTrabajadorCommandHandler implements ICommandHandler<AsignarGrupoTrabajadorCommand> {

    private final ITrabajadorService serviceImpl;

    public AsignarGrupoTrabajadorCommandHandler(ITrabajadorService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    @Transactional("writeTransactionManager")
    public void handle(AsignarGrupoTrabajadorCommand command) {
        TrabajadorDto dto = serviceImpl.findById(command.getTrabajadorId());
        dto.setGrupoId(command.getGrupoId());
        serviceImpl.update(dto);
    }
}
