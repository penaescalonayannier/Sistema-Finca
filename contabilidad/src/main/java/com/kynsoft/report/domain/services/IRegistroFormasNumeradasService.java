package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.AnularEmisionFormaNumeradaRequest;
import com.kynsoft.report.domain.dto.EmisionFormaNumeradaDto;
import com.kynsoft.report.domain.dto.EmitirFormaNumeradaRequest;
import com.kynsoft.report.domain.dto.RegistroFormaNumeradaDto;

import java.util.List;
import java.util.UUID;

/**
 * Motor independiente para las formas numeradas. No reemplaza las secuencias
 * históricas hasta que cada flujo documental se integre explícitamente.
 */
public interface IRegistroFormasNumeradasService {
    EmisionFormaNumeradaDto emitir(EmitirFormaNumeradaRequest request);
    List<RegistroFormaNumeradaDto> consultar(UUID fincaId, Integer anio);
    List<EmisionFormaNumeradaDto> consultarEmisiones(String codigoForma, UUID fincaId, Integer anio);
    EmisionFormaNumeradaDto anular(UUID emisionId, AnularEmisionFormaNumeradaRequest request);
    EmisionFormaNumeradaDto registrarReimpresion(UUID emisionId, UUID usuarioId);
}
