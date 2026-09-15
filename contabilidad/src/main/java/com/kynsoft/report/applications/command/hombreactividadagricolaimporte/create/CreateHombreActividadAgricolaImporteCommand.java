package com.kynsoft.report.applications.command.hombreactividadagricolaimporte.create;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateHombreActividadAgricolaImporteCommand implements ICommand {
    private UUID id;
    private UUID trabajador;
    private UUID labor;
    private UUID instrumento;
    private UUID bloque;
    private UUID campo;
    private BigDecimal dias;
    private BigDecimal horas;
    private BigDecimal norma;
    private BigDecimal tasa;
    private BigDecimal importe;

    public static CreateHombreActividadAgricolaImporteCommand fromRequest(CreateHombreActividadAgricolaImporteRequest request) {
        return new CreateHombreActividadAgricolaImporteCommand(
                UUID.randomUUID(),
                request.getTrabajador(),
                request.getLabor(),
                request.getInstrumento(),
                request.getBloque(),
                request.getCampo(),
                request.getDias(),
                request.getHoras(),
                request.getNorma(),
                request.getTasa(),
                request.getImporte()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateHombreActividadAgricolaImporteMessage(id);
    }
}