package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.DeudaTrabajadorDto;
import com.kynsoft.report.domain.dto.FormaPago;
import com.kynsoft.report.domain.dto.MovimientoDeudaResult;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IDeudaTrabajadorService {

    UUID create(DeudaTrabajadorDto dto);

    void update(DeudaTrabajadorDto dto);

    void delete(UUID id);

    DeudaTrabajadorDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    DeudaTrabajadorDto findByTrabajadorId(UUID trabajadorId);

    /**
     * Incrementa la deuda de un trabajador (usado desde Salida).
     * Solo incrementa si pagado = false.
     */
    void incrementarDeuda(UUID trabajadorId, Double importe);

    /**
     * Registra un pago de deuda.
     * - Valida monto > 0
     * - Valida monto <= deuda actual (RN-02)
     * - Valida referencia bancaria si formaPago = TRANSFERENCIA (RN-03)
     */
    MovimientoDeudaResult registrarPago(UUID trabajadorId, Double monto, FormaPago formaPago,
                                         String referenciaBancaria, String observaciones);

    /**
     * Registra un ajuste contable.
     * - Valida monto != 0
     * - Valida observaciones no vacías
     * - Valida no dejar saldo negativo (RN-07)
     */
    MovimientoDeudaResult registrarAjuste(UUID trabajadorId, Double monto, String observaciones);

    /**
     * Registra carga inicial de deuda (migración).
     * - Valida monto > 0
     * - Valida observaciones obligatorias (RN-08)
     */
    MovimientoDeudaResult registrarCargaInicial(UUID trabajadorId, Double monto, String observaciones);
}
