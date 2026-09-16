package com.kynsoft.report.applications.command.almacenproducto.entrada;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.report.domain.dto.AlmacenFincaProductoDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.services.IAlmacenFincaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EntradaAlmacenCommandHandler implements ICommandHandler<EntradaAlmacenCommand> {

    private final IAlmacenFincaProductoService service;

    @Override
    public void handle(EntradaAlmacenCommand command) {
        TipoMovimientoStock tipo = command.getTipo();
        if (tipo == null) {
            throw validationError("tipo", "Debe indicar el tipo de entrada.");
        }

        switch (tipo) {
            case ENTRADA_PRODUCCION:
                // La producción terminada exige responsables que entregan y reciben,
                // además del documento SC-2-06. No puede registrarse como una
                // entrada genérica porque dejaría inventario sin documento fuente.
                throw validationError("tipo", "La entrada por producción debe registrarse mediante Producción Terminada.");
            case ENTRADA_FACTURA:
                if (isBlank(command.getNumeroFactura())) {
                    throw validationError("numeroFactura", "El número de factura es obligatorio para esta entrada.");
                }
                service.entradaFactura(command.getAlmacenFincaProductoId(),
                        command.getCantidad(), command.getNumeroFactura(), command.getDescripcion());
                break;
            case ENTRADA_CONDUCE:
                if (isBlank(command.getNumeroConduce())) {
                    throw validationError("numeroConduce", "El número de conduce es obligatorio para esta entrada.");
                }
                service.entradaConduce(command.getAlmacenFincaProductoId(),
                        command.getCantidad(), "Conduce: " + command.getNumeroConduce()
                                + (isBlank(command.getDescripcion()) ? "" : " - " + command.getDescripcion()));
                break;
            case ENTRADA_AJUSTE:
                service.entrada(command.getAlmacenFincaProductoId(),
                        command.getCantidad(), tipo, command.getDescripcion(), command.getCentroCosto());
                break;
            default:
                throw validationError("tipo", "El tipo indicado no corresponde a una entrada de almacén.");
        }

        AlmacenFincaProductoDto updated = service.findById(command.getAlmacenFincaProductoId());
        command.setStockNuevo(updated.getStock());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private BusinessNotFoundException validationError(String field, String message) {
        return new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND, new ErrorField(field, message)));
    }
}
