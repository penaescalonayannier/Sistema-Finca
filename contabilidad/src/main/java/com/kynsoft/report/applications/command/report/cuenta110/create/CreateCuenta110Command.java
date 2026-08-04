package com.kynsoft.report.applications.command.report.cuenta110.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateCuenta110Command implements ICommand {

    private UUID id;
    private String observaciones;
    private Double importe;

    public CreateCuenta110Command(String observaciones, Double importe) {
        this.id = UUID.randomUUID();
        this.observaciones = observaciones;
        this.importe = importe;
    }

    public static CreateCuenta110Command fromRequest(CreateCuenta110Request request) {
        return new CreateCuenta110Command(
                request.getObservaciones(),
                request.getImporte()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateCuenta110Message(id);
    }
}
