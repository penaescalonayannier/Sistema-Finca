package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ResponsableConsolidadoDto {
    private UUID trabajadorResponsableId;
    private String trabajadorResponsableNombre;
    private List<TrabajadorConsolidadoDto> trabajadores;
}
