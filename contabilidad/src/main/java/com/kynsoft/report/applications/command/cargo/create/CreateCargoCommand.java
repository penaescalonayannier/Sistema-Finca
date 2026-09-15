package com.kynsoft.report.applications.command.cargo.create;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreateCargoCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal salarioEscala;

    public CreateCargoCommand(String name, String description, BigDecimal salarioEscala) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.salarioEscala = salarioEscala;
    }

    public static CreateCargoCommand fromRequest(CreateCargoRequest request) {
        return new CreateCargoCommand(
                request.getName(),
                request.getDescription(),
                request.getSalarioEscala()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateCargoMessage(id);
    }
}
