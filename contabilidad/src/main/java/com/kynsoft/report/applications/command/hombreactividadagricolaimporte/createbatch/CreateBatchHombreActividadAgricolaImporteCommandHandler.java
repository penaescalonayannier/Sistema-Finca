package com.kynsoft.report.applications.command.hombreactividadagricolaimporte.createbatch;

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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CreateBatchHombreActividadAgricolaImporteCommandHandler implements ICommandHandler<CreateBatchHombreActividadAgricolaImporteCommand> {

    private final IHombreActividadAgricolaImporteService serviceImpl;
    private final ITrabajadorService trabajadorService;
    private final ILaborService laborService;
    private final IInstrumentoTrabajoService instrumentoTrabajoService;
    private final IBloqueService bloqueService;
    private final ICamposService campoService;

    @Override
    @Transactional
    public void handle(CreateBatchHombreActividadAgricolaImporteCommand command) {
        List<UUID> createdIds = new ArrayList<>();
        
        for (BatchHombreActividadAgricolaImporteItem item : command.getItems()) {
            // Buscar las entidades relacionadas
            TrabajadorDto trabajadorDto = this.trabajadorService.findById(item.getTrabajador());
            LaborDto laborDto = this.laborService.findById(item.getLabor());
            InstrumentoTrabajoDto instrumentoDto = this.instrumentoTrabajoService.findById(item.getInstrumento());
            BloqueDto bloqueDto = this.bloqueService.findById(item.getBloque());
            CampoDto campoDto = this.campoService.findById(item.getCampo());

            // Asignar ID si no viene en el item
            UUID id = item.getId() != null ? item.getId() : UUID.randomUUID();

            // Crear el DTO
            HombreActividadAgricolaImporteDto dto = HombreActividadAgricolaImporteDto.builder()
                    .id(id)
                    .trabajador(trabajadorDto)
                    .labor(laborDto)
                    .instrumento(instrumentoDto)
                    .bloque(bloqueDto)
                    .campo(campoDto)
                    .dias(item.getDias())
                    .horas(item.getHoras())
                    .norma(item.getNorma())
                    .tasa(item.getTasa())
                    .importe(item.getImporte())
                    .build();

            // Guardar
            serviceImpl.create(dto);
            createdIds.add(id);
        }
    }
}