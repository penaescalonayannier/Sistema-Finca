package com.kynsoft.report.infrastructure.services.reporte;

import com.kynsoft.report.domain.dto.report.confColor.Conf;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FooterData {
    private PDType1Font fontBold;
    private float baseLineHeight;
    private Conf color;
}
