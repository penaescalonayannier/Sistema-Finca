package com.kynsoft.report.applications.command.fincaproducto.asignar;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AsignarProductoAFincaCommand implements ICommand {
    private UUID fincaId;
    private UUID productoId;
    private Double stock;
    private Double stockMinimo;
    private UUID id; // Set by handler after creation

    public AsignarProductoAFincaCommand(UUID fincaId, UUID productoId, Double stock, Double stockMinimo) {
        this.fincaId = fincaId;
        this.productoId = productoId;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    public static AsignarProductoAFincaCommand fromRequest(AsignarProductoAFincaRequest request) {
        return new AsignarProductoAFincaCommand(
                request.getFincaId(),
                request.getProductoId(),
                request.getStock(),
                request.getStockMinimo()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new AsignarProductoAFincaMessage(id);
    }
}
