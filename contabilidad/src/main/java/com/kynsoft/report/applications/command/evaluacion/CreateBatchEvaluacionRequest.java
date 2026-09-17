package com.kynsoft.report.applications.command.evaluacion;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBatchEvaluacionRequest {
    private String mes;
    private Integer year;
    private UUID grupoId;
    private UUID jefeId;
    private String evidencia;
    private String criteriosAplicados;
    private String constanciaJefe;
    private List<CreateBatchEvaluacionItem> evaluaciones;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateBatchEvaluacionItem {
        private UUID trabajadorId;
        private Integer calificacion;
        private String comentarios;
        private String constanciaTrabajador;
    }
}
