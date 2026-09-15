package com.kynsoft.report.applications.command.grupo.delete;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IGrupoService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteGrupoCommandHandler implements ICommandHandler<DeleteGrupoCommand> {

    private final IGrupoService grupoService;
    private final ITrabajadorService trabajadorService;

    public DeleteGrupoCommandHandler(IGrupoService grupoService, ITrabajadorService trabajadorService) {
        this.grupoService = grupoService;
        this.trabajadorService = trabajadorService;
    }

    @Override
    @Transactional("writeTransactionManager")
    public void handle(DeleteGrupoCommand command) {
        // Desasignar trabajadores del grupo antes de eliminarlo
        trabajadorService.desasignarTrabajadoresDeGrupo(command.getId());
        grupoService.delete(command.getId());
    }
}
