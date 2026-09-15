package com.kynsoft.report.applications.command.campos.update;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.BloqueDto;
import com.kynsoft.report.domain.dto.CampoDto;
import com.kynsoft.report.domain.dto.CepaDto;
import com.kynsoft.report.domain.dto.VariedadDto;
import com.kynsoft.report.domain.services.IBloqueService;
import com.kynsoft.report.domain.services.ICamposService;
import com.kynsoft.report.domain.services.ICepaService;
import com.kynsoft.report.domain.services.IVariedadService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateCamposCommandHandler implements ICommandHandler<UpdateCamposCommand> {

    private final ICamposService serviceImpl;
    private final IBloqueService bloqueService;
    private final IVariedadService variedadService;
    private final ICepaService cepaService;

    @Override
    public void handle(UpdateCamposCommand command) {
        // 1. Buscar la entidad existente por ID
        CampoDto dto = serviceImpl.findById(command.getId());
        BloqueDto bloqueDto = this.bloqueService.findById(command.getBloque());
        VariedadDto variedadDto = this.variedadService.findById(command.getVariedad());
        CepaDto cepa = this.cepaService.findById(command.getCepa());

        // 2. Crear un nuevo DTO con las propiedades actualizadas
        CampoDto updatedDto = CampoDto.builder()
                .id(dto.getId())
                .bloque(bloqueDto)
                .campo(command.getCampo())
                .area(command.getArea())
                .variedad(variedadDto)
                .cepa(cepa)
                .poblacion(command.getPoblacion())
                .destino(command.getDestino())
                .rendimiento(command.getRendimiento())
                .valorAdquisicion(command.getValorAdquisicion())
                .depreciacionAcumulada(command.getDepreciacionAcumulada())
                .valorResidual(command.getValorResidual())
                .anosCepa(command.getAnosCepa())
                .tasaDepreciacionAnual(command.getTasaDepreciacionAnual())
                .vidaUtilAnios(command.getVidaUtilAnios())
                .fechaInicioDepreciacion(command.getFechaInicioDepreciacion())
                .fechaUltimaDepreciacion(dto.getFechaUltimaDepreciacion())
                .build();

        // 3. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }
}