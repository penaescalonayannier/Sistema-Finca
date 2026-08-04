package com.kynsoft.report.applications.command.diatrabajo.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.DiaTrabajoDto;
import com.kynsoft.report.domain.services.IDiaTrabajoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateDiaTrabajoCommandHandler 
    implements ICommandHandler<CreateDiaTrabajoCommand> {

    private final IDiaTrabajoService diaTrabajoService;

    @Override
    public void handle(CreateDiaTrabajoCommand command) {
        DiaTrabajoDto dto = DiaTrabajoDto.builder()
                .id(command.getId())
                .reporteId(command.getReporteId())
                .fecha(command.getFecha())
                .build();
        
        diaTrabajoService.create(dto);
    }
}