package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.TipoDocumento;
import com.kynsoft.report.domain.dto.EstadoConsecutivoDocumentoDto;

import java.util.UUID;
import java.util.List;

public interface INumeracionService {

    /**
     * Genera el siguiente número de documento para una finca y tipo específico.
     * Este método es thread-safe y maneja la concurrencia usando bloqueo pesimista.
     *
     * @param fincaId ID de la finca
     * @param tipo Tipo de documento (VALE, FACTURA, RECIBO)
     * @return Número generado en formato: PREFIJO-AÑO-SECUENCIA (ej: VALE-2026-00001)
     */
    String generarSiguienteNumero(UUID fincaId, TipoDocumento tipo);

    /**
     * Obtiene el último número usado sin incrementar.
     *
     * @param fincaId ID de la finca
     * @param tipo Tipo de documento
     * @param anio Año
     * @return Último número usado o 0 si no existe configuración
     */
    Integer obtenerUltimoNumero(UUID fincaId, TipoDocumento tipo, Integer anio);

    /**
     * Verifica la integridad de la secuencia de numeración.
     * Detecta huecos en la secuencia.
     *
     * @param fincaId ID de la finca
     * @param tipo Tipo de documento
     * @param anio Año a verificar
     * @return true si la secuencia está completa, false si hay huecos
     */
    boolean verificarIntegridadSecuencia(UUID fincaId, TipoDocumento tipo, Integer anio);

    /** Consulta inmutable de consecutivos oficiales por finca y año. */
    List<EstadoConsecutivoDocumentoDto> obtenerEstadoDocumentos(UUID fincaId, Integer anio);
}
