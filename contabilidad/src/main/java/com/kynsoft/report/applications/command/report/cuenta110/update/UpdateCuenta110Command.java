package com.kynsoft.report.applications.command.report.cuenta110.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateCuenta110Command implements ICommand {

    private UUID id;
    private String observaciones;
    private Double importe;

    public UpdateCuenta110Command(UUID id, String observaciones, Double importe) {
        this.id = id;
        this.observaciones = observaciones;
        this.importe = importe;
    }

    public static UpdateCuenta110Command fromRequest(UpdateCuenta110Request request, UUID id) {
        return new UpdateCuenta110Command(
                id,
                request.getObservaciones(),
                request.getImporte()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateCuenta110Message(id);
    }
}
