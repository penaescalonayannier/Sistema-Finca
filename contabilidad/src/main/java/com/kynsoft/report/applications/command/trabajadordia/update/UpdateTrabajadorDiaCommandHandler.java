package com.kynsoft.report.applications.command.trabajadordia.update;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.TrabajadorDiaDto;
import com.kynsoft.report.domain.services.ITrabajadorDiaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateTrabajadorDiaCommandHandler 
    implements ICommandHandler<UpdateTrabajadorDiaCommand> {

    private final ITrabajadorDiaService trabajadorDiaService;

    @Override
    public void handle(UpdateTrabajadorDiaCommand command) {
        // Primero obtener el existente para mantener el diaTrabajoId y trabajadorId
        TrabajadorDiaDto existente = trabajadorDiaService.findById(command.getId());
        
        TrabajadorDiaDto dto = TrabajadorDiaDto.builder()
                .id(command.getId())
                .diaTrabajoId(existente.getDiaTrabajoId())
                .trabajadorId(existente.getTrabajadorId())
                .horas(command.getHoras())
                .norma(command.getNorma())
                .build();
        
        trabajadorDiaService.update(dto);
    }
}