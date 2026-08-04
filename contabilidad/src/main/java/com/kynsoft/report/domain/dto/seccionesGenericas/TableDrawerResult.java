package com.kynsoft.report.domain.dto.seccionesGenericas;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

@Data
@AllArgsConstructor
public class TableDrawerResult {
    private float yPosition;
    private PDPageContentStream contentStream;
}