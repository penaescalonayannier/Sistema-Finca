package com.kynsoft.report.domain.dto.seccionesGenericas;

import com.kynsoft.report.domain.dto.report.confColor.Conf;
import lombok.Builder;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.util.List;
import lombok.Data;

@Builder
@Data
public class TableSectionData {
    private float[] columnWidths;
    private String[] headers;
    private List<TableRowData> rows;
    private boolean[] headerColoredCells;
    private boolean[] headerBoldCells;
    private PDType1Font fontRegular;
    private PDType1Font fontBold;
    private float fontSize;
    private Conf color;
    private float startY;
    private float margin;
    private float pageHeight;
    private float pageWidth;
    private float currentX;
    private float minFooterHeight;
    private float baseLineHeight;
    
    // Nuevos campos configurables para líneas
    @Builder.Default
    private boolean drawLineAboveHeader = false;
    
    @Builder.Default
    private boolean drawLineBelowHeader = true;
    
    @Builder.Default
    private float lineOffset = -15f; // Offset para ajustar posición de líneas
}