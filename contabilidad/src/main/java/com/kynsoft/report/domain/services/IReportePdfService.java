package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.ReportePdfDto;

public interface IReportePdfService {
    byte[] generarPdfReporte(ReportePdfDto data);
}