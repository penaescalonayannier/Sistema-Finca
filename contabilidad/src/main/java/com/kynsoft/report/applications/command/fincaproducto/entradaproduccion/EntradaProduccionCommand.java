package com.kynsoft.report.applications.command.fincaproducto.entradaproduccion;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class EntradaProduccionCommand implements ICommand {
    private UUID fincaId;
    private UUID productoId;
    private Integer cantidad;
    private String descripcion;
    private String centroCosto;

    public static EntradaProduccionCommand fromRequest(EntradaProduccionRequest request) {
        return new EntradaProduccionCommand(
                request.getFincaId(),
                request.getProductoId(),
                request.getCantidad(),
                request.getDescripcion(),
                request.getCentroCosto()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new EntradaProduccionMessage(fincaId, productoId, cantidad);
    }
}
