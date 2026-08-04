package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.ReporteConsolidadoPorResponsablePdfDto;

public interface IReporteConsolidadoPorResponsablePdfService {
    byte[] generarPdfConsolidadoPorResponsable(ReporteConsolidadoPorResponsablePdfDto data);
}
