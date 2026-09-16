package com.kynsoft.report.domain.dto;

import lombok.Builder;
import lombok.Getter;

/** Estado de una secuencia documental; es solamente de consulta. */
@Getter
@Builder
public class EstadoConsecutivoDocumentoDto {
    private final TipoDocumento tipo;
    private final String prefijo;
    private final Integer anio;
    private final Integer ultimoNumero;
    private final String proximoNumero;
    private final long cantidadDocumentos;
    private final boolean integridad;
}
