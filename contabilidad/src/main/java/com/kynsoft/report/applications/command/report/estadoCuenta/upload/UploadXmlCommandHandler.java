package com.kynsoft.report.applications.command.report.estadoCuenta.upload;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.EstadoCuentaDto;
import com.kynsoft.report.domain.dto.Cuenta110EfectivoBancoDto; // Importar el DTO de la cuenta 110
import com.kynsoft.report.domain.services.IEstadoCuentaService;
import com.kynsoft.report.domain.services.ICuenta110EfectivoBancoService; // Importar el servicio de la cuenta 110
import com.kynsoft.report.infrastructure.util.XmlParserUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // Requerido para operaciones que modifican múltiples entidades

import java.util.List;

@Component
public class UploadXmlCommandHandler implements ICommandHandler<UploadXmlCommand> {

    private final IEstadoCuentaService estadoCuentaServiceImpl;
    private final ICuenta110EfectivoBancoService cuenta110ServiceImpl; // Inyección del nuevo servicio

    // Modificar el constructor para inyectar ambos servicios
    public UploadXmlCommandHandler(IEstadoCuentaService estadoCuentaServiceImpl,
            ICuenta110EfectivoBancoService cuenta110ServiceImpl) {
        this.estadoCuentaServiceImpl = estadoCuentaServiceImpl;
        this.cuenta110ServiceImpl = cuenta110ServiceImpl;
    }

    @Override
    @Transactional // Aseguramos que todas las operaciones (EstadoCuenta y Cuenta110) sean atómicas
    public void handle(UploadXmlCommand command) {
        try {
            // 1. Ejecutar el parsing del XML
            List<EstadoCuentaDto> dtos = XmlParserUtil.parse(command.getXmlContent());

            // 2. Obtener el DTO de Cuenta110 (el único registro)
            Cuenta110EfectivoBancoDto cuenta110Dto = cuenta110ServiceImpl.findUnique();
            Double nuevoImporte = cuenta110Dto.getImporte();

            // 3. Iterar sobre la lista, persistir EstadoCuenta y actualizar el saldo de Cuenta110
            for (EstadoCuentaDto estadoCuentaDto : dtos) {

                // Asignar ID si es necesario y persistir el EstadoCuenta
                if (estadoCuentaDto.getId() == null) {
                    estadoCuentaDto.setId(java.util.UUID.randomUUID());
                }
                estadoCuentaServiceImpl.create(estadoCuentaDto);

                // Lógica para actualizar el importe de Cuenta110
                if (estadoCuentaDto.getImporte() != null) {
                    if ("Cr".equalsIgnoreCase(estadoCuentaDto.getTipo())) {
                        // Crédito (Cr) aumenta el importe
                        nuevoImporte += estadoCuentaDto.getImporte();
                    } else if ("Db".equalsIgnoreCase(estadoCuentaDto.getTipo())) {
                        // Débito (Db) disminuye el importe
                        nuevoImporte -= estadoCuentaDto.getImporte();
                    }
                }
            }

            // 4. Actualizar el registro único de Cuenta110 en la base de datos
            cuenta110Dto.setImporte(nuevoImporte);
            cuenta110ServiceImpl.update(cuenta110Dto);

        } catch (Exception e) {
            // Manejar si el registro único de Cuenta110 no existe (caso de uso del método findUnique)
            throw new RuntimeException("Error crítico: No se puede actualizar el saldo. El registro único de Cuenta110 no existe o hay un problema de integridad. " + e.getMessage(), e);
        }
    }
}
