package com.kynsoft.report.domain.services;

import com.kynsoft.report.applications.query.responseObject.*;

import java.util.List;
import java.util.UUID;

public interface IReportesMetricasService {

    /**
     * Calcula métricas de ausentismo para un período
     * @param year Año en formato "2024"
     * @param mes Mes en formato "Enero", "Febrero", etc.
     * @param trabajadorId Opcional: filtrar por trabajador específico
     * @return Lista de AbsentismoResponse
     */
    List<AbsentismoResponse> calcularAbsentismo(String year, String mes, UUID trabajadorId);

    /**
     * Calcula métricas de productividad para un período
     * @param year Año en formato "2024"
     * @param mes Mes en formato "Enero", "Febrero", etc.
     * @param trabajadorId Opcional: filtrar por trabajador específico
     * @return Lista de ProductividadResponse
     */
    List<ProductividadResponse> calcularProductividad(String year, String mes, UUID trabajadorId);

    /**
     * Calcula rankings de productividad por cargo
     * @param year Año en formato "2024"
     * @param mes Mes en formato "Enero", "Febrero", etc.
     * @param cargo Opcional: filtrar por cargo específico
     * @return Lista de RankingResponse (uno por cargo)
     */
    List<RankingResponse> calcularRankings(String year, String mes, String cargo);

    /**
     * Calcula resumen de horas excedidas
     * @param year Año en formato "2024"
     * @param mes Mes en formato "Enero", "Febrero", etc.
     * @return Lista de HorasExcedidasSummaryResponse
     */
    List<HorasExcedidasSummaryResponse> calcularHorasExcedidasSummary(String year, String mes);
}
