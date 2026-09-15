package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionNumeracionDto {

    private UUID id;
    private UUID fincaId;
    private String fincaCode;
    private String fincaName;
    private TipoDocumento tipo;
    private String prefijo;
    private Integer anio;
    private Integer ultimoNumero;
}
