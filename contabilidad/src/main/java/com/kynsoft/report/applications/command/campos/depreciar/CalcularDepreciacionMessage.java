package com.kynsoft.report.applications.command.campos.depreciar;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CalcularDepreciacionMessage implements ICommandMessage {
    private int camposActualizados;

    public static CalcularDepreciacionMessage success(int camposActualizados) {
        return new CalcularDepreciacionMessage(camposActualizados);
    }
}
