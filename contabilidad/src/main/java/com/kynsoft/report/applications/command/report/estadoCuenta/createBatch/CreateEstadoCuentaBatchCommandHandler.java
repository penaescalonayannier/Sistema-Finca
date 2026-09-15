package com.kynsoft.report.applications.command.report.estadoCuenta.createBatch;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.applications.command.report.estadoCuenta.create.CreateEstadoCuentaRequest;
import com.kynsoft.report.domain.dto.EstadoCuentaDto;
import com.kynsoft.report.domain.dto.Cuenta110EfectivoBancoDto;
import com.kynsoft.report.domain.services.IEstadoCuentaService;
import com.kynsoft.report.domain.services.ICuenta110EfectivoBancoService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class CreateEstadoCuentaBatchCommandHandler implements ICommandHandler<CreateEstadoCuentaBatchCommand> {

    private final IEstadoCuentaService estadoCuentaService;
    private final ICuenta110EfectivoBancoService cuenta110Service;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CreateEstadoCuentaBatchCommandHandler(
            IEstadoCuentaService estadoCuentaService,
            ICuenta110EfectivoBancoService cuenta110Service) {
        this.estadoCuentaService = estadoCuentaService;
        this.cuenta110Service = cuenta110Service;
    }

    @Override
    @Transactional
    public void handle(CreateEstadoCuentaBatchCommand command) {
        try {
            // 1. Obtener el registro único de Cuenta110 al inicio
            Cuenta110EfectivoBancoDto cuenta110Dto = cuenta110Service.findUnique();
            Double saldoActual = cuenta110Dto.getImporte();
            Double nuevoSaldo = saldoActual; // Inicializar con el saldo actual

            // 2. Iterar sobre las operaciones y procesarlas
            for (CreateEstadoCuentaRequest request : command.getOperaciones()) {
                UUID newId = UUID.randomUUID();

                // Parsear fecha
                LocalDate fecha = null;
                if (request.getFecha() != null && !request.getFecha().isEmpty()) {
                    try {
                        fecha = LocalDate.parse(request.getFecha(), DATE_FORMATTER);
                    } catch (Exception e) {
                        // Si falla el parsing, intentar con otro formato
                        try {
                            fecha = LocalDate.parse(request.getFecha());
                        } catch (Exception ex) {
                            // Dejar fecha como null si no se puede parsear
                        }
                    }
                }

                // Crear DTO de EstadoCuenta
                EstadoCuentaDto dto = EstadoCuentaDto.builder()
                        .id(newId)
                        .fecha(fecha)
                        .refOrigen(request.getRefOrigen())
                        .refCorriente(request.getRefCorriente())
                        .observaciones(request.getObservaciones())
                        .tipo(request.getType())
                        .importe(request.getImporte())
                        .clienteId(request.getClienteId())
                        .build();

                // 3. Persistir el EstadoCuenta
                estadoCuentaService.create(dto);

                // 4. Actualizar el saldo acumulado de Cuenta110
                if (dto.getImporte() != null) {
                    if ("Cr".equalsIgnoreCase(dto.getTipo())) {
                        // Crédito (Cr) aumenta el saldo
                        nuevoSaldo += dto.getImporte();
                    } else if ("Db".equalsIgnoreCase(dto.getTipo())) {
                        // Débito (Db) disminuye el saldo
                        nuevoSaldo -= dto.getImporte();
                    }
                }

                // 5. Agregar el ID creado a la lista
                command.getCreatedIds().add(newId);
            }

            // 6. Validar que el saldo no sea negativo (opcional, según reglas de negocio)
            if (nuevoSaldo < 0) {
                throw new RuntimeException("El saldo de la cuenta 110 no puede ser negativo. Saldo resultante: " + nuevoSaldo);
            }

            // 7. Actualizar el registro único de Cuenta110 con el nuevo saldo
            cuenta110Dto.setImporte(nuevoSaldo);
            cuenta110Service.update(cuenta110Dto);

        } catch (Exception e) {
            // Manejar errores específicos
            if (e.getMessage().contains("No se puede actualizar el saldo")) {
                throw new RuntimeException("Error crítico al procesar el batch: " + e.getMessage(), e);
            } else if (e.getMessage().contains("no puede ser negativo")) {
                throw new RuntimeException("Error de validación: " + e.getMessage(), e);
            } else {
                throw new RuntimeException("Error inesperado al procesar el batch: " + e.getMessage(), e);
            }
        }
    }
}