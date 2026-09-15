package com.kynsoft.report.applications.command.almacenproducto.salidamultiple;

import com.kynsoft.report.domain.dto.DestinoSalida;
import com.kynsoft.report.domain.dto.LineaSalidaMultipleAlmacenDto;
import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SalidaMultipleAlmacenCommand implements ICommand {
    private UUID almacenId;
    private DestinoSalida destino;
    private String observaciones;
    private List<LineaSalidaMultipleAlmacenDto> lineas;
    private List<UUID> salidaIds;

    public SalidaMultipleAlmacenCommand(UUID almacenId, DestinoSalida destino, String observaciones,
                                        List<LineaSalidaMultipleAlmacenDto> lineas) {
        this.almacenId = almacenId;
        this.destino = destino;
        this.observaciones = observaciones;
        this.lineas = lineas;
    }

    public static SalidaMultipleAlmacenCommand fromRequest(UUID almacenId, SalidaMultipleAlmacenRequest request) {
        return new SalidaMultipleAlmacenCommand(almacenId, request.getDestino(), request.getObservaciones(), request.getLineas());
    }

    @Override
    public ICommandMessage getMessage() {
        return new SalidaMultipleAlmacenMessage(salidaIds, lineas == null ? 0 : lineas.size());
    }
}
