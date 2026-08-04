package com.kynsoft.report.applications.command.trabajadorReporte.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.TrabajadorReporteDto;
import com.kynsoft.report.domain.services.ITrabajadorReporteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateTrabajadorReporteCommandHandler implements ICommandHandler<CreateTrabajadorReporteCommand> {

    private final ITrabajadorReporteService reportService;

    @Override
    public void handle(CreateTrabajadorReporteCommand command) {
        reportService.asignarTrabajadorAReporte(TrabajadorReporteDto.builder()
                .id(command.getId())
                .trabajador(command.getTrabajador())
                .reporte(command.getReporte())
                .norma(command.getNorma())
                .horas(command.getHoras())
                .build()
        );
    }
}
