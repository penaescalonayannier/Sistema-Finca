package com.kynsoft.report.applications.command.reporte.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateReporteCommand implements ICommand {

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

    public static UpdateReporteCommand fromRequest(UpdateReporteRequest request, UUID id) {
        return new UpdateReporteCommand(
                id,
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
        return new UpdateReporteMessage(id);
    }
}
