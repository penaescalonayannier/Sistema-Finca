package com.kynsoft.report.applications.command.grupo.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.services.IGrupoService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteGrupoCommandHandler implements ICommandHandler<DeleteGrupoCommand> {

    private final IGrupoService serviceImpl;

    public DeleteGrupoCommandHandler(IGrupoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    @Transactional("writeTransactionManager")
    public void handle(DeleteGrupoCommand command) {
        serviceImpl.delete(command.getId());
    }
}
