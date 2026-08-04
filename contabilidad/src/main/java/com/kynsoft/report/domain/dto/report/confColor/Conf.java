package com.kynsoft.report.domain.dto.report.confColor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Conf {

    @Builder.Default
    private float red = 230f / 255f;

    @Builder.Default
    private float green = 230f / 255f;

    @Builder.Default
    private float blue = 255f / 255f;

    @Builder.Default
    private float fontSize = 9f;

    @Builder.Default
    private float fontSizeEncabezados = 9f;

    @Builder.Default
    private float fontSizeMayusculas = 9f;

    @Builder.Default
    private float fontSizeMayusculasMinusculas = 10.5f;
}
