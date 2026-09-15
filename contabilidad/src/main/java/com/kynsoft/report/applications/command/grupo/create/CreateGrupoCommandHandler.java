package com.kynsoft.report.applications.command.grupo.create;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.GrupoDto;
import com.kynsoft.report.domain.services.IGrupoService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateGrupoCommandHandler implements ICommandHandler<CreateGrupoCommand> {

    private final IGrupoService serviceImpl;

    public CreateGrupoCommandHandler(IGrupoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    @Transactional("writeTransactionManager")
    public void handle(CreateGrupoCommand command) {
        serviceImpl.create(GrupoDto
                .builder()
                .id(command.getId())
                .nombre(command.getNombre())
                .descripcion(command.getDescripcion())
                .jefeId(command.getJefeId())
                .build());
    }
}
