package com.kynsoft.report.applications.command.reporte.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateReporteCommand implements ICommand {

    private UUID id;
    private String bloque;
    private String campo;
    private String area;
    private String norma;
    private String fecha;
    private String codigo;
    private String year;
    private String mes;
    private UUID trabajadorResponsableId;

    public static CreateReporteCommand fromRequest(CreateReporteRequest request) {
        return new CreateReporteCommand(
                UUID.randomUUID(),
                request.getBloque(),
                request.getCampo(),
                request.getArea(),
                request.getNorma(),
                request.getFecha(),
                request.getCodigo(),
                request.getYear(),
                request.getMes(),
                request.getTrabajadorResponsableId()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateReporteMessage(id);
    }
}
