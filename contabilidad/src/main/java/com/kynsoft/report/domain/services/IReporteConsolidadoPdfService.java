package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.ReporteConsolidadoPdfDto;

public interface IReporteConsolidadoPdfService {
    byte[] generarPdfConsolidado(ReporteConsolidadoPdfDto data);
}