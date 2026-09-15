package com.kynsoft.report.applications.command.report.cuenta110.update.importe;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.Cuenta110EfectivoBancoDto;
import com.kynsoft.report.domain.services.ICuenta110EfectivoBancoService;
import org.springframework.stereotype.Component;

@Component
public class UpdateCuenta110ImporteCommandHandler implements ICommandHandler<UpdateCuenta110ImporteCommand> {

    private final ICuenta110EfectivoBancoService serviceImpl;

    public UpdateCuenta110ImporteCommandHandler(ICuenta110EfectivoBancoService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(UpdateCuenta110ImporteCommand command) {
        
        // 1. Buscar el único DTO existente
        Cuenta110EfectivoBancoDto dto = serviceImpl.findUnique();

        if (dto == null) {
            // Si el único registro no existe, lanza una excepción de no encontrado.
            throw new RuntimeException("No se encontró el registro único de Cuenta110 para actualizar.");
        }

        // 2. Aplicar el nuevo importe al DTO encontrado
        dto.setImporte(command.getImporte());
        
        // 3. Persistir la actualización
        serviceImpl.update(dto);

        // 4. (Opcional) Establecer el ID en el comando para el mensaje de respuesta.
        command.setId(dto.getId());
    }
}