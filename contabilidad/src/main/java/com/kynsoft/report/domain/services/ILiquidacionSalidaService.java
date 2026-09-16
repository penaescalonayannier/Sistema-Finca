package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.EntregaBancoRequest;
import com.kynsoft.report.domain.dto.LiquidarSalidaRequest;
import com.kynsoft.report.domain.dto.SaldoCajaDto;
import com.kynsoft.report.domain.dto.SalidaPendienteLiquidacionDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ILiquidacionSalidaService {
    UUID liquidar(LiquidarSalidaRequest request);
    List<SalidaPendienteLiquidacionDto> pendientes(UUID fincaId, LocalDate fechaInicio, LocalDate fechaFin);
    SaldoCajaDto obtenerSaldoCaja(UUID fincaId);
    UUID entregarBanco(EntregaBancoRequest request);
}
