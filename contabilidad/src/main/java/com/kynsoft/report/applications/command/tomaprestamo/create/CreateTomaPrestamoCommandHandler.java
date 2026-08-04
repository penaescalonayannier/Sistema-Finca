package com.kynsoft.report.applications.command.tomaprestamo.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.report.domain.dto.PrestamoDto;
import com.kynsoft.report.domain.dto.TomaPrestamoDto;
import com.kynsoft.report.domain.services.IPrestamoService;
import com.kynsoft.report.domain.services.ITomaPrestamoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateTomaPrestamoCommandHandler implements ICommandHandler<CreateTomaPrestamoCommand> {

    private final ITomaPrestamoService tomaPrestamoService;
    private final IPrestamoService prestamoService;

    public CreateTomaPrestamoCommandHandler(ITomaPrestamoService tomaPrestamoService,
            IPrestamoService prestamoService) {
        this.tomaPrestamoService = tomaPrestamoService;
        this.prestamoService = prestamoService;
    }

    @Override
    @Transactional
    public void handle(CreateTomaPrestamoCommand command) {
        // Validar que el importe sea positivo
        if (command.getImporte() <= 0) {
            throw new IllegalArgumentException("El importe debe ser un valor positivo.");
        }

        // 1. Obtener el préstamo por su ID
        UUID creditoId = UUID.fromString(command.getCreditoId());
        PrestamoDto prestamo = prestamoService.findById(creditoId);
        Double importeUtilizadoEfectivo = prestamo.getImporteUtilizadoEfectivo() != null ? prestamo.getImporteUtilizadoEfectivo() : 0.00;
        Double importeUtilizadoSuministros = prestamo.getImporteUtilizadoSuministros()!= null ? prestamo.getImporteUtilizadoSuministros(): 0.00;
        Double importeUtilizadoSeguro = prestamo.getImporteUtilizadoSeguro()!= null ? prestamo.getImporteUtilizadoSeguro(): 0.00;

        // 2. Actualizar los campos según el tipo
        String tipo = command.getTipo().name();
        Double importe = command.getImporte();

        switch (tipo.toUpperCase()) {
            case "EFECTIVO" -> {
                // Verificar que hay importe aprobado para efectivo
                if (prestamo.getImporteAprobadoEfectivo() == null || prestamo.getImporteAprobadoEfectivo() <= 0) {
                    throw new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("importeAprobadoEfectivo", "No hay importe aprobado para efectivo en este préstamo.")
                    ));
                }

                // Inicializar si es null
                if (prestamo.getImporteUtilizadoEfectivo() == null) {
                    prestamo.setImporteUtilizadoEfectivo(0.0);
                }

                // Verificar que no exceda el importe aprobado
                double nuevoUtilizadoEfectivo = prestamo.getImporteUtilizadoEfectivo() + importe;
                double disponibleEfectivo = prestamo.getImporteAprobadoEfectivo() - prestamo.getImporteUtilizadoEfectivo();

                if (nuevoUtilizadoEfectivo > prestamo.getImporteAprobadoEfectivo()) {
                    throw new IllegalArgumentException(
                            String.format(
                                    "El importe excede el monto disponible para efectivo. "
                                    + "Aprobado: %.2f, Utilizado actual: %.2f, Disponible: %.2f, Nuevo monto: %.2f",
                                    prestamo.getImporteAprobadoEfectivo(),
                                    prestamo.getImporteUtilizadoEfectivo(),
                                    disponibleEfectivo,
                                    importe
                            )
                    );
                }
                prestamo.setImporteUtilizadoEfectivo(nuevoUtilizadoEfectivo);
            }

            case "SUMINISTRO" -> {
                // Verificar que hay importe aprobado para suministros
                if (prestamo.getImporteAprobadoSuministros() == null || prestamo.getImporteAprobadoSuministros() <= 0) {
                    throw new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("importeAprobadoSuministros", "No hay importe aprobado para suministros en este préstamo.")
                    ));
                }

                if (prestamo.getImporteUtilizadoSuministros() == null) {
                    prestamo.setImporteUtilizadoSuministros(0.0);
                }

                // Verificar que no exceda el importe aprobado
                double nuevoUtilizadoSuministros = prestamo.getImporteUtilizadoSuministros() + importe;
                double disponibleSuministros = prestamo.getImporteAprobadoSuministros() - prestamo.getImporteUtilizadoSuministros();

                if (nuevoUtilizadoSuministros > prestamo.getImporteAprobadoSuministros()) {
                    throw new IllegalArgumentException(
                            String.format(
                                    "El importe excede el monto disponible para suministros. "
                                    + "Aprobado: %.2f, Utilizado actual: %.2f, Disponible: %.2f, Nuevo monto: %.2f",
                                    prestamo.getImporteAprobadoSuministros(),
                                    prestamo.getImporteUtilizadoSuministros(),
                                    disponibleSuministros,
                                    importe
                            )
                    );
                }
                prestamo.setImporteUtilizadoSuministros(nuevoUtilizadoSuministros);
            }

            case "SEGURO" -> {
                // Verificar que hay importe aprobado para seguro
                if (prestamo.getImporteAprobadoSeguro() == null || prestamo.getImporteAprobadoSeguro() <= 0) {
                    throw new BusinessNotFoundException(new GlobalBusinessException(
                            DomainErrorMessage.BUSINESS_NOT_FOUND,
                            new ErrorField("importeAprobadoSeguro", "No hay importe aprobado para seguro en este préstamo.")
                    ));
                }

                if (prestamo.getImporteUtilizadoSeguro() == null) {
                    prestamo.setImporteUtilizadoSeguro(0.0);
                }

                // Verificar que no exceda el importe aprobado
                double nuevoUtilizadoSeguro = prestamo.getImporteUtilizadoSeguro() + importe;
                double disponibleSeguro = prestamo.getImporteAprobadoSeguro() - prestamo.getImporteUtilizadoSeguro();

                if (nuevoUtilizadoSeguro > prestamo.getImporteAprobadoSeguro()) {
                    throw new IllegalArgumentException(
                            String.format(
                                    "El importe excede el monto disponible para seguro. "
                                    + "Aprobado: %.2f, Utilizado actual: %.2f, Disponible: %.2f, Nuevo monto: %.2f",
                                    prestamo.getImporteAprobadoSeguro(),
                                    prestamo.getImporteUtilizadoSeguro(),
                                    disponibleSeguro,
                                    importe
                            )
                    );
                }
                prestamo.setImporteUtilizadoSeguro(nuevoUtilizadoSeguro);
            }

            default ->
                throw new IllegalArgumentException("Tipo de toma no válido: " + tipo);
        }

        // 3. Actualizar el préstamo en la base de datos
        prestamoService.update(prestamo);

        // 4. Crear la toma de préstamo
        TomaPrestamoDto tomaDto = TomaPrestamoDto.builder()
                .id(command.getId())
                .importe(command.getImporte())
                .fecha(command.getFecha())
                .cuentaDestino(command.getCuentaDestino())
                .tipo(command.getTipo())
                .observaciones(command.getObservaciones())
                .creditoId(command.getCreditoId())
                .importeUtilizadoEfectivo(importeUtilizadoEfectivo)
                .importeUtilizadoSuministros(importeUtilizadoSuministros)
                .importeUtilizadoSeguro(importeUtilizadoSeguro)
                .build();

        tomaPrestamoService.create(tomaDto);
    }
}
