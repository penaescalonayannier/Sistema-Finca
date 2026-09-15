package com.kynsoft.report.applications.command.trabajadordia.create;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.TrabajadorDiaDto;
import com.kynsoft.report.domain.services.ITrabajadorDiaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateTrabajadorDiaCommandHandler 
    implements ICommandHandler<CreateTrabajadorDiaCommand> {

    private final ITrabajadorDiaService trabajadorDiaService;

    @Override
    public void handle(CreateTrabajadorDiaCommand command) {
        TrabajadorDiaDto dto = TrabajadorDiaDto.builder()
                .id(command.getId())
                .diaTrabajoId(command.getDiaTrabajoId())
                .trabajadorId(command.getTrabajadorId())
                .horas(command.getHoras())
                .norma(command.getNorma())
                .build();
        
        trabajadorDiaService.create(dto);
    }
}