package com.kynsoft.report.applications.command.hombreactividadagricolaimporte.create;

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
public class CreateHombreActividadAgricolaImporteCommandHandler implements ICommandHandler<CreateHombreActividadAgricolaImporteCommand> {

    private final IHombreActividadAgricolaImporteService serviceImpl;
    private final ITrabajadorService trabajadorService;
    private final ILaborService laborService;
    private final IInstrumentoTrabajoService instrumentoTrabajoService;
    private final IBloqueService bloqueService;
    private final ICamposService campoService;

    @Override
    public void handle(CreateHombreActividadAgricolaImporteCommand command) {
        // Buscar las entidades relacionadas
        TrabajadorDto trabajadorDto = this.trabajadorService.findById(command.getTrabajador());
        LaborDto laborDto = this.laborService.findById(command.getLabor());
        InstrumentoTrabajoDto instrumentoDto = this.instrumentoTrabajoService.findById(command.getInstrumento());
        BloqueDto bloqueDto = this.bloqueService.findById(command.getBloque());
        CampoDto campoDto = this.campoService.findById(command.getCampo());

        // Crear el DTO con las relaciones
        serviceImpl.create(HombreActividadAgricolaImporteDto.builder()
                .id(command.getId())
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
                .build());
    }
}