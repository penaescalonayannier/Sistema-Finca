package com.kynsoft.report.applications.command.prestamo.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.PrestamoDto;
import com.kynsoft.report.domain.services.IPrestamoService;
import org.springframework.stereotype.Component;

@Component
public class UpdatePrestamoCommandHandler implements ICommandHandler<UpdatePrestamoCommand> {

    private final IPrestamoService serviceImpl;

    public UpdatePrestamoCommandHandler(IPrestamoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(UpdatePrestamoCommand command) {
        serviceImpl.update(PrestamoDto
                .builder()
                .id(command.getId())
                .importeAprobado(command.getImporteAprobado())
                .importeAprobadoEfectivo(command.getImporteAprobadoEfectivo())
                .importeUtilizadoEfectivo(command.getImporteUtilizadoEfectivo())
                .importeAprobadoSuministros(command.getImporteAprobadoSuministros())
                .importeUtilizadoSuministros(command.getImporteUtilizadoSuministros())
                .importeAprobadoSeguro(command.getImporteAprobadoSeguro())
                .importeUtilizadoSeguro(command.getImporteUtilizadoSeguro())
                .numeroContrato(command.getNumeroContrato())
                .cuenta(command.getCuenta())
                .observaciones(command.getObservaciones())
                .toneladasMolibles(command.getToneladasMolibles())
                .build());
    }
}
