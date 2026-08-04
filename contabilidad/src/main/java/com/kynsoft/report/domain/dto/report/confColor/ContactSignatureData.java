package com.kynsoft.report.domain.dto.report.confColor;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactSignatureData {
    private String phones;
    private String email;
    private String website;
    private PDType1Font fontRegular;
    private PDType1Font fontBold;
    private float fontSize;
    private Conf color;
}