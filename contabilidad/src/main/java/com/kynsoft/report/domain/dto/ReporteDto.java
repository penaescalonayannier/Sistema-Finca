package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ReporteDto {
    private UUID id;

    // Tipo de reporte (centro de costo)
    private UUID tipoReporteId;
    private String tipoReporteCodigo;
    private String tipoReporteNombre;
    private TipoSubclasificacion tipoSubclasificacion;
    private CategoriaTipoCultivo tipoCultivoCategoriaFiltro;

    // Tipo de cultivo
    private UUID tipoCultivoId;
    private String tipoCultivoNombre;
    private Boolean tipoCultivoRequiereCampo;

    // Tipo de animal (para vaquería)
    private UUID tipoAnimalId;
    private String tipoAnimalNombre;

    // Bloque y campo (opcionales según tipo de cultivo)
    private String bloque;
    private String campo;
    private String area;

    private String norma;
    private String codigo;
    private String year;
    private String mes;
    private String fecha;
    private UUID trabajadorResponsableId;
    private String trabajadorResponsableNombre;
    private Boolean activo;
}
