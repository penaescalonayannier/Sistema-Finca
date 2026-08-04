package com.kynsoft.report.applications.command.cargo.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class UpdateCargoCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal salarioEscala;

    public UpdateCargoCommand(UUID id, String name, String description, BigDecimal salarioEscala) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.salarioEscala = salarioEscala;
    }

    public static UpdateCargoCommand fromRequest(UpdateCargoRequest request) {
        return new UpdateCargoCommand(
                request.getId(),
                request.getName(),
                request.getDescription(),
                request.getSalarioEscala()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateCargoMessage(id);
    }
}
