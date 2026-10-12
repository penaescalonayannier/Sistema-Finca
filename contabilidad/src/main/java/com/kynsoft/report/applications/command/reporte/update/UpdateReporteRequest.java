package com.kynsoft.report.applications.command.reporte.update;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class UpdateReporteRequest {
    private String bloque;
    private String campo;
    private String area;
    private String norma;
    private String fecha;
    private String codigo;
    private String year;
    private String mes;
    private UUID trabajadorResponsableId;
    private UUID tipoReporteId;
    private UUID tipoCultivoId;
    private UUID tipoAnimalId;
    private UUID productoId;
    private Double cantidadCosechada;
    /** Se ignora al guardar: se toma de la unidad vigente del producto. */
    private String unidadMedidaSnapshot;
    /** Se ignora al guardar: se toma del precio seleccionado del producto. */
    private Double precioUnitarioReferencia;
    private String tipoPrecioReferencia;
}
