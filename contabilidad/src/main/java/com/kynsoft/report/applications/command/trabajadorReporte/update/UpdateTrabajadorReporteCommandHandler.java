package com.kynsoft.report.applications.command.trabajadorReporte.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.TrabajadorReporteDto;
import com.kynsoft.report.domain.services.ITrabajadorReporteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateTrabajadorReporteCommandHandler implements ICommandHandler<UpdateTrabajadorReporteCommand> {

    private final ITrabajadorReporteService reportService;

    @Override
    public void handle(UpdateTrabajadorReporteCommand command) {
        reportService.actualizarTrabajadorEnReporte(TrabajadorReporteDto.builder()
                .id(command.getId())
                .norma(command.getNorma())
                .horas(command.getHoras())
                .build()
        );
    }
}
