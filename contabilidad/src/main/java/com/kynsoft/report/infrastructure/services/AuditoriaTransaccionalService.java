package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.services.IAuditoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

/**
 * Publica la bitácora solo después de confirmar la transacción de negocio.
 * Así no se registran como hechos movimientos que finalmente fueron revertidos.
 */
@Service
public class AuditoriaTransaccionalService {

    private final IAuditoriaService auditoriaService;

    public AuditoriaTransaccionalService(IAuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    public void registrarDespuesDeConfirmar(TipoAccion accion, String entidad, UUID entidadId,
                                             String descripcion, Object valorAnterior, Object valorNuevo) {
        Runnable registrar = () -> auditoriaService.registrar(
                accion, entidad, entidadId, descripcion, valorAnterior, valorNuevo);
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    registrar.run();
                }
            });
            return;
        }
        registrar.run();
    }
}
