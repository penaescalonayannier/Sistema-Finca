package com.kynsoft.report.applications.command.hombreactividadagricolaimporte.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.BloqueDto;
import com.kynsoft.report.domain.dto.CampoDto;
import com.kynsoft.report.domain.dto.HombreActividadAgricolaImporteDto;
import com.kynsoft.report.domain.dto.InstrumentoTrabajoDto;
import com.kynsoft.report.domain.dto.LaborDto;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.services.IBloqueService;
import com.kynsoft.report.domain.services.ICamposService;
import com.kynsoft.report.domain.services.IHombreActividadAgricolaImporteService;
import com.kynsoft.report.domain.services.IInstrumentoTrabajoService;
import com.kynsoft.report.domain.services.ILaborService;
import com.kynsoft.report.domain.services.ITrabajadorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateHombreActividadAgricolaImporteCommandHandler implements ICommandHandler<UpdateHombreActividadAgricolaImporteCommand> {

    private final IHombreActividadAgricolaImporteService serviceImpl;
    private final ITrabajadorService trabajadorService;
    private final ILaborService laborService;
    private final IInstrumentoTrabajoService instrumentoTrabajoService;
    private final IBloqueService bloqueService;
    private final ICamposService campoService;

    @Override
    public void handle(UpdateHombreActividadAgricolaImporteCommand command) {
        // 1. Buscar la entidad existente por ID
        HombreActividadAgricolaImporteDto dto = serviceImpl.findById(command.getId());

        // 2. Buscar las entidades relacionadas actualizadas
        TrabajadorDto trabajadorDto = this.trabajadorService.findById(command.getTrabajador());
        LaborDto laborDto = this.laborService.findById(command.getLabor());
        InstrumentoTrabajoDto instrumentoDto = this.instrumentoTrabajoService.findById(command.getInstrumento());
        BloqueDto bloqueDto = this.bloqueService.findById(command.getBloque());
        CampoDto campoDto = this.campoService.findById(command.getCampo());

        // 3. Crear un nuevo DTO con las propiedades actualizadas
        HombreActividadAgricolaImporteDto updatedDto = HombreActividadAgricolaImporteDto.builder()
                .id(dto.getId())
                .trabajador(trabajadorDto)
                .labor(laborDto)
                .instrumento(instrumentoDto)
                .bloque(bloqueDto)
                .campo(campoDto)
                .dias(command.getDias())
                .horas(command.getHoras())
                .norma(command.getNorma())
                .tasa(command.getTasa())
                .importe(command.getImporte())
                .build();

        // 4. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }
}