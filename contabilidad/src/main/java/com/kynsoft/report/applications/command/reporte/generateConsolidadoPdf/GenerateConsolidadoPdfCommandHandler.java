package com.kynsoft.report.applications.command.reporte.generateConsolidadoPdf;

import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.ReporteConsolidadoDto;
import com.kynsoft.report.domain.dto.ReporteConsolidadoPdfDto;
import com.kynsoft.report.domain.services.IReporteConsolidadoPdfService;
import com.kynsoft.report.domain.services.IReporteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class GenerateConsolidadoPdfCommandHandler 
    implements ICommandHandler<GenerateConsolidadoPdfCommand> {

    private final IReporteService reporteService;
    private final IReporteConsolidadoPdfService reporteConsolidadoPdfService;

    @Override
    public void handle(GenerateConsolidadoPdfCommand command) {
        try {
            // 1. Obtener el consolidado
            ReporteConsolidadoDto consolidado = reporteService.getConsolidado(command.getYear(), command.getMes());
            
            // 2. Calcular días del mes
            int diasDelMes = getDaysInMonth(command.getYear(), command.getMes());
            
            // 3. Construir DTO para el PDF
            ReporteConsolidadoPdfDto pdfData = ReporteConsolidadoPdfDto.builder()
                    .year(command.getYear())
                    .mes(command.getMes())
                    .trabajadores(consolidado.getTrabajadores())
                    .diasDelMes(diasDelMes)
                    .build();
            
            // 4. Generar el PDF
            byte[] pdfBytes = reporteConsolidadoPdfService.generarPdfConsolidado(pdfData);
            
            command.setPdfData(pdfBytes);
            // 5. Crear el mensaje con los datos del PDF
            //return new GenerateConsolidadoPdfMessage(command.getYear(), command.getMes(), pdfBytes);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el PDF del consolidado: " + e.getMessage(), e);
        }
    }
    
    private int getDaysInMonth(String year, String mes) {
        int yearInt = Integer.parseInt(year);
        int mesInt = getMonthNumber(mes);
        YearMonth yearMonth = YearMonth.of(yearInt, mesInt);
        return yearMonth.lengthOfMonth();
    }
    
    private int getMonthNumber(String mes) {
        Map<String, Integer> meses = new HashMap<>();
        meses.put("Enero", 1);
        meses.put("Febrero", 2);
        meses.put("Marzo", 3);
        meses.put("Abril", 4);
        meses.put("Mayo", 5);
        meses.put("Junio", 6);
        meses.put("Julio", 7);
        meses.put("Agosto", 8);
        meses.put("Septiembre", 9);
        meses.put("Octubre", 10);
        meses.put("Noviembre", 11);
        meses.put("Diciembre", 12);
        return meses.getOrDefault(mes, 1);
    }
}