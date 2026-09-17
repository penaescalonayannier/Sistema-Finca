package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import lombok.Builder;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class TrabajadorDto {

    private UUID id;
    private String ruc; // CI
    private String nombre; // NOMBRE
    private String cuenta; // CUENTA_E
    private Boolean activo; // Activo/Inactivo
    private UUID fincaId; // Finca a la que pertenece
    private String fincaCode; // Código de la finca
    private String fincaName; // Nombre de la finca
    private UUID grupoId; // Grupo al que pertenece
    private String grupoNombre; // Nombre del grupo
    private UUID cargoId; // ID del cargo
    private String cargoName; // Nombre del cargo
    /** Opcional: la plantilla formal es gradual; los trabajadores históricos pueden no tener plaza. */
    private UUID plazaId;
    private String plazaCodigo;
    private ReporteDto reporte;
}
