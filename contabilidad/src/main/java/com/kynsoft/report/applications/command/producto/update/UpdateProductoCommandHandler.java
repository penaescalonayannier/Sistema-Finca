package com.kynsoft.report.applications.command.producto.update;

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
public class UpdateProductoCommandHandler implements ICommandHandler<UpdateProductoCommand> {

    private final IProductoService serviceImpl;

    // Patrón para validar código alfanumérico (solo letras y números)
    private static final Pattern CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    @Override
    public void handle(UpdateProductoCommand command) {
        // 1. Buscar la entidad existente por ID
        ProductoDto dto = serviceImpl.findById(command.getId());

        // 2. Validar código si se proporciona
        String finalCode = command.getCode() != null ? command.getCode() : dto.getCode();
        if (command.getCode() != null) {
            validateCode(command.getCode());
        }

        // 3. Obtener precios finales
        Double finalPrice = command.getPrice() != null ? command.getPrice() : dto.getPrice();
        Double finalPriceTrabajador = command.getPriceTrabajador() != null ? command.getPriceTrabajador() : dto.getPriceTrabajador();
        Double finalPriceComedor = command.getPriceComedor() != null ? command.getPriceComedor() : dto.getPriceComedor();

        // 4. Validar precios (no pueden ser negativos)
        validatePricesNotNegative(finalPrice, finalPriceTrabajador, finalPriceComedor);

        // 5. Validar stock
        Integer finalStock = command.getStock() != null ? command.getStock() : dto.getStock();
        validateStock(finalStock);

        // 6. Determinar estado activo
        // RN-02: Si algún precio es 0, desactivar automáticamente
        Boolean finalActive = command.getActive() != null ? command.getActive() : dto.getActive();
        if (finalPrice == 0 || finalPriceTrabajador == 0 || finalPriceComedor == 0) {
            finalActive = false;
        }

        // 7. Crear DTO actualizado
        ProductoDto updatedDto = ProductoDto.builder()
                .id(dto.getId())
                .code(finalCode)
                .name(command.getName() != null ? command.getName() : dto.getName())
                .description(command.getDescription() != null ? command.getDescription() : dto.getDescription())
                .price(finalPrice)
                .priceTrabajador(finalPriceTrabajador)
                .priceComedor(finalPriceComedor)
                .stock(finalStock)
                .active(finalActive)
                .unidadMedida(command.getUnidadMedida() != null ? command.getUnidadMedida() : dto.getUnidadMedida())
                .tipoProducto(command.getTipoProducto() != null ? command.getTipoProducto() : dto.getTipoProducto())
                .build();

        // 8. Llamar al servicio para persistir la actualización
        serviceImpl.update(updatedDto);
    }

    private void validateCode(String code) {
        if (code.trim().isEmpty()) {
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

    private void validatePricesNotNegative(Double price, Double priceTrabajador, Double priceComedor) {
        if (price < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("price", "El precio no puede ser negativo.")));
        }

        if (priceTrabajador < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("priceTrabajador", "El precio para trabajador no puede ser negativo.")));
        }

        if (priceComedor < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("priceComedor", "El precio para comedor no puede ser negativo.")));
        }
    }

    private void validateStock(Integer stock) {
        if (stock < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("stock", "El stock no puede ser negativo.")));
        }
    }
}