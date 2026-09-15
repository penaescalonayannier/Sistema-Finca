package com.kynsoft.report.applications.command.prestamo.create;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.PrestamoDto;
import com.kynsoft.report.domain.services.IPrestamoService;
import org.springframework.stereotype.Component;

@Component
public class CreatePrestamoCommandHandler implements ICommandHandler<CreatePrestamoCommand> {

    private final IPrestamoService serviceImpl;

    public CreatePrestamoCommandHandler(IPrestamoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(CreatePrestamoCommand command) {
        serviceImpl.create(PrestamoDto
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
