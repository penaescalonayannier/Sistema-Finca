package com.kynsoft.report.applications.command.producto.create;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.share.core.domain.exception.BusinessNotFoundException;
import com.kynsoft.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.share.core.domain.exception.GlobalBusinessException;
import com.kynsoft.share.core.domain.response.ErrorField;
import com.kynsoft.report.domain.dto.ProductoDto;
import com.kynsoft.report.domain.services.IProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class CreateProductoCommandHandler implements ICommandHandler<CreateProductoCommand> {

    private final IProductoService serviceImpl;

    // Patrón para validar código alfanumérico (solo letras y números)
    private static final Pattern CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    @Override
    public void handle(CreateProductoCommand command) {
        // Validar código alfanumérico
        validateCode(command.getCode());

        // Validar precios > 0
        validatePrices(command.getPrice(), command.getPriceTrabajador(), command.getPriceComedor());

        // Validar stock >= 0
        validateStock(command.getStock());

        serviceImpl.create(ProductoDto.builder()
                .id(command.getId())
                .code(command.getCode())
                .name(command.getName())
                .description(command.getDescription())
                .price(command.getPrice())
                .priceTrabajador(command.getPriceTrabajador())
                .priceComedor(command.getPriceComedor())
                .stock(command.getStock())
                .active(command.getActive())
                .unidadMedida(command.getUnidadMedida())
                .tipoProducto(command.getTipoProducto())
                .build());
    }

    private void validateCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("code", "El código es requerido.")));
        }

        if (!CODE_PATTERN.matcher(code).matches()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("code", "El código solo puede contener letras y números (sin espacios ni caracteres especiales).")));
        }
    }

    private void validatePrices(Double price, Double priceTrabajador, Double priceComedor) {
        if (price == null || price <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("price", "El precio debe ser mayor a 0.")));
        }

        if (priceTrabajador == null || priceTrabajador <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("priceTrabajador", "El precio para trabajador debe ser mayor a 0.")));
        }

        if (priceComedor == null || priceComedor <= 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("priceComedor", "El precio para comedor debe ser mayor a 0.")));
        }
    }

    private void validateStock(Integer stock) {
        if (stock == null || stock < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("stock", "El stock no puede ser negativo.")));
        }
    }
}