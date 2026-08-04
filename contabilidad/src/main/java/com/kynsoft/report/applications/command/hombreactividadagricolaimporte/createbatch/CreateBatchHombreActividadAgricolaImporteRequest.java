package com.kynsoft.report.applications.command.hombreactividadagricolaimporte.createbatch;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateBatchHombreActividadAgricolaImporteRequest {
    private List<BatchHombreActividadAgricolaImporteItem> items;
}