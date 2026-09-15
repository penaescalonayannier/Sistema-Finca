package com.kynsoft.report.applications.command.campos.depreciar;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CalcularDepreciacionCommand implements ICommand {
    private List<UUID> campoIds;
    private Integer meses;

    public static CalcularDepreciacionCommand fromRequest(CalcularDepreciacionRequest request) {
        return new CalcularDepreciacionCommand(
                request.getCampoIds(),
                request.getMeses() != null ? request.getMeses() : 1
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CalcularDepreciacionMessage(campoIds.size());
    }
}
