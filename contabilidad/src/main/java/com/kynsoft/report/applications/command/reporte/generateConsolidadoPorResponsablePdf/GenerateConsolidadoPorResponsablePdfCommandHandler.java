package com.kynsoft.report.applications.command.reporte.generateConsolidadoPorResponsablePdf;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.ReporteConsolidadoPorResponsablePdfDto;
import com.kynsoft.report.domain.services.IReporteConsolidadoPorResponsablePdfService;
import com.kynsoft.report.domain.services.IReporteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GenerateConsolidadoPorResponsablePdfCommandHandler
    implements ICommandHandler<GenerateConsolidadoPorResponsablePdfCommand> {

    private final IReporteService reporteService;
    private final IReporteConsolidadoPorResponsablePdfService pdfService;

    @Override
    public void handle(GenerateConsolidadoPorResponsablePdfCommand command) {
        try {
            // 1. Obtener el consolidado por responsable
            ReporteConsolidadoPorResponsablePdfDto pdfData = reporteService.getConsolidadoPorResponsable(
                    command.getYear(),
                    command.getMes()
            );

            // 2. Generar el PDF
            byte[] pdfBytes = pdfService.generarPdfConsolidadoPorResponsable(pdfData);

            command.setPdfData(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el PDF del consolidado por responsable: " + e.getMessage(), e);
        }
    }
}
