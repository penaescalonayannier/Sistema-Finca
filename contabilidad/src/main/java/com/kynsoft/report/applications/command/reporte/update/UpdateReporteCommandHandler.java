package com.kynsoft.report.applications.command.reporte.update;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.ReporteDto;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.IReporteService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateReporteCommandHandler implements ICommandHandler<UpdateReporteCommand> {

    private final IReporteService reportService;
    private final ITrabajadorService trabajadorService;

    @Override
    public void handle(UpdateReporteCommand command) {
        // Obtener el reporte existente para mantener los días
        ReporteDto existente = reportService.findById(command.getId());

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

        ReporteDto dto = ReporteDto.builder()
                .id(command.getId())
                .bloque(command.getBloque() != null ? command.getBloque() : existente.getBloque())
                .campo(command.getCampo() != null ? command.getCampo() : existente.getCampo())
                .area(command.getArea() != null ? command.getArea() : existente.getArea())
                .norma(command.getNorma() != null ? command.getNorma() : existente.getNorma())
                .codigo(command.getCodigo() != null ? command.getCodigo() : existente.getCodigo())
                .year(command.getYear() != null ? command.getYear() : existente.getYear())
                .mes(command.getMes() != null ? command.getMes() : existente.getMes())
                .fecha(command.getFecha() != null ? command.getFecha() : existente.getFecha())
                .trabajadorResponsableId(command.getTrabajadorResponsableId() != null ? command.getTrabajadorResponsableId() : existente.getTrabajadorResponsableId())
                .trabajadorResponsableNombre(trabajadorResponsableNombre)
                .build();

        reportService.update(dto);
    }
}