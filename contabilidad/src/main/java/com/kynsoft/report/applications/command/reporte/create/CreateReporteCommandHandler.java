package com.kynsoft.report.applications.command.reporte.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.ReporteDto;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.IReporteService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateReporteCommandHandler implements ICommandHandler<CreateReporteCommand> {

    private final IReporteService reportService;
    private final ITrabajadorService trabajadorService;

    @Override
    public void handle(CreateReporteCommand command) {
        String trabajadorResponsableNombre = null;

        // El trabajadorResponsableId es obligatorio
        if (command.getTrabajadorResponsableId() == null) {
            throw new IllegalArgumentException("El trabajador responsable es obligatorio");
        }

        try {
            TrabajadorDto trabajador = trabajadorService.findById(command.getTrabajadorResponsableId());
            if (trabajador != null) {
                trabajadorResponsableNombre = trabajador.getNombre();
            } else {
                throw new IllegalArgumentException("El trabajador seleccionado no existe");
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new IllegalArgumentException("Error al obtener el trabajador responsable: " + e.getMessage());
        }

        reportService.create(ReporteDto.builder()
                .id(command.getId())
                .bloque(command.getBloque())
                .campo(command.getCampo())
                .area(command.getArea())
                .norma(command.getNorma())
                .fecha(command.getFecha())
                .codigo(command.getCodigo())
                .year(command.getYear())
                .mes(command.getMes())
                .trabajadorResponsableId(command.getTrabajadorResponsableId())
                .trabajadorResponsableNombre(trabajadorResponsableNombre)
                .build());
    }
}