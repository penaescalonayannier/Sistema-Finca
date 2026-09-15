package com.kynsoft.report.applications.command.salida.update;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.domain.dto.ItemSalidaDto;
import com.kynsoft.report.domain.dto.TipoSalida;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateSalidaCommand implements ICommand {

    private UUID id;
    private TipoSalida tipo;
    private UUID fincaProductoId;
    private String observaciones;
    private List<ItemSalidaDto> items;

    public UpdateSalidaCommand(UUID id, TipoSalida tipo, UUID fincaProductoId, String observaciones, List<ItemSalidaDto> items) {
        this.id = id;
        this.tipo = tipo;
        this.fincaProductoId = fincaProductoId;
        this.observaciones = observaciones;
        this.items = items;
    }

    public static UpdateSalidaCommand fromRequest(UpdateSalidaRequest request) {
        return new UpdateSalidaCommand(
                request.getId(),
                request.getTipo(),
                request.getFincaProductoId(),
                request.getObservaciones(),
                request.getItems()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateSalidaMessage(id);
    }
}
