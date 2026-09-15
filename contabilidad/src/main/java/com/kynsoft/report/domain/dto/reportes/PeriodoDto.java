package com.kynsoft.report.domain.dto.reportes;

import lombok.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class PeriodoDto {
    private LocalDate inicio;
    private LocalDate fin;
}
