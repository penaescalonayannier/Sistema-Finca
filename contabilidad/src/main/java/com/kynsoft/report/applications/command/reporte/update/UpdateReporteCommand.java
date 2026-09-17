package com.kynsoft.report.applications.command.reporte.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
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
    private UUID tipoReporteId;
    private UUID tipoCultivoId;
    private UUID tipoAnimalId;

    /** Conserva compatibilidad con las integraciones que aún no envían clasificación. */
    public UpdateReporteCommand(UUID id, String bloque, String campo, String area, String norma,
            String fecha, String codigo, String year, String mes, UUID trabajadorResponsableId) {
        this(id, bloque, campo, area, norma, fecha, codigo, year, mes, trabajadorResponsableId,
                null, null, null);
    }

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
                request.getTrabajadorResponsableId(),
                request.getTipoReporteId(),
                request.getTipoCultivoId(),
                request.getTipoAnimalId()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateReporteMessage(id);
    }
}
