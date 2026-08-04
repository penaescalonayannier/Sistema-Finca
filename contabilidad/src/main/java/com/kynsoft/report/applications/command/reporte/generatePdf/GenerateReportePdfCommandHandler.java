package com.kynsoft.report.applications.command.reporte.generatePdf;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.DiaTrabajoDto;
import com.kynsoft.report.domain.dto.ReporteDto;
import com.kynsoft.report.domain.dto.ReportePdfDto;
import com.kynsoft.report.domain.services.IReportePdfService;
import com.kynsoft.report.domain.services.IReporteService;
import com.kynsoft.report.domain.services.IDiaTrabajoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class GenerateReportePdfCommandHandler 
    implements ICommandHandler<GenerateReportePdfCommand> {

    private final IReporteService reporteService;
    private final IDiaTrabajoService diaTrabajoService;
    private final IReportePdfService reportePdfService;

    @Override
    public void handle(GenerateReportePdfCommand command) {
        try {
            // 1. Obtener el reporte
            ReporteDto reporte = reporteService.findById(command.getReporteId());
            
            // 2. Obtener los días del reporte con sus trabajadores
            List<DiaTrabajoDto> dias = diaTrabajoService.findByReporteIdWithTrabajadores(command.getReporteId());
            
            // 3. Construir el DTO para el PDF
            ReportePdfDto pdfData = ReportePdfDto.builder()
                    .reporte(reporte)
                    .dias(dias)
                    .build();
            
            // 4. Generar el PDF
            byte[] pdfBytes = reportePdfService.generarPdfReporte(pdfData);
            
            // 5. Crear el mensaje con los datos del PDF
            command.setPdfData(pdfBytes);
            //return new GenerateReportePdfMessage(command.getReporteId(), pdfBytes);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage(), e);
        }
    }
}