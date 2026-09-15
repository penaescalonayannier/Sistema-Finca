package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.MovimientoDepreciacionDto;
import com.kynsoft.report.domain.services.IDepreciacionService;
import com.kynsoft.report.domain.services.IDepreciacionService.ReporteDepreciacionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller REST para gestión de Depreciación de Activos Fijos.
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP): Método de depreciación línea recta
 * - Resolución 51/2021 MFP: Tasas máximas de depreciación por grupo
 *
 * Fórmula de cálculo según NCC No. 7:
 * - Depreciación Anual = Valor Adquisición × Tasa%
 * - Depreciación Mensual = Depreciación Anual / 12
 *
 * El cierre mensual de depreciación:
 * 1. Calcula depreciación para cada activo activo
 * 2. Registra el movimiento con fecha del período
 * 3. Actualiza depreciación acumulada y valor residual
 */
@RestController
@RequestMapping("/api/depreciacion")
public class DepreciacionController {

    private final IDepreciacionService depreciacionService;

    public DepreciacionController(IDepreciacionService depreciacionService) {
        this.depreciacionService = depreciacionService;
    }

    /**
     * Calcula la depreciación mensual para un activo específico.
     * No ejecuta el cierre, solo calcula el valor.
     */
    @GetMapping("/calcular/{activoFijoId}")
    public ResponseEntity<Map<String, BigDecimal>> calcularDepreciacion(
            @PathVariable UUID activoFijoId) {
        BigDecimal depreciacion = depreciacionService.calcularDepreciacionMensual(activoFijoId);
        return ResponseEntity.ok(Map.of("depreciacionMensual", depreciacion));
    }

    /**
     * Ejecuta el cierre mensual de depreciación para todos los activos.
     * Solo se permite un cierre por mes/año.
     *
     * @param mes Mes del cierre (1-12)
     * @param anio Año del cierre
     * @return Lista de movimientos generados
     */
    @PostMapping("/cierre-mensual")
    public ResponseEntity<?> ejecutarCierreMensual(@RequestBody Map<String, Integer> body) {
        Integer mes = body.get("mes");
        Integer anio = body.get("anio");

        if (mes == null || anio == null || mes < 1 || mes > 12) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Debe especificar mes (1-12) y año válidos"));
        }

        try {
            List<MovimientoDepreciacionDto> movimientos =
                    depreciacionService.ejecutarCierreMensual(mes, anio);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Cierre de depreciación ejecutado para " + mes + "/" + anio,
                    "movimientos", movimientos,
                    "totalMovimientos", movimientos.size()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Ejecuta el cierre de depreciación para un activo específico.
     */
    @PostMapping("/cierre-activo/{activoFijoId}")
    public ResponseEntity<?> ejecutarCierreActivo(@PathVariable UUID activoFijoId,
                                                   @RequestBody Map<String, Integer> body) {
        Integer mes = body.get("mes");
        Integer anio = body.get("anio");

        if (mes == null || anio == null || mes < 1 || mes > 12) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Debe especificar mes (1-12) y año válidos"));
        }

        MovimientoDepreciacionDto movimiento =
                depreciacionService.ejecutarCierreActivo(activoFijoId, mes, anio);

        if (movimiento == null) {
            return ResponseEntity.ok(Map.of(
                    "mensaje", "No se generó movimiento (activo totalmente depreciado o ya procesado)"
            ));
        }

        return ResponseEntity.ok(movimiento);
    }

    /**
     * Obtiene el historial de depreciación de un activo.
     */
    @GetMapping("/activo/{activoFijoId}")
    public ResponseEntity<List<MovimientoDepreciacionDto>> findByActivoFijo(
            @PathVariable UUID activoFijoId) {
        List<MovimientoDepreciacionDto> movimientos =
                depreciacionService.findByActivoFijo(activoFijoId);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Obtiene todos los movimientos de depreciación de un período.
     */
    @GetMapping("/periodo/{mes}/{anio}")
    public ResponseEntity<List<MovimientoDepreciacionDto>> findByPeriodo(
            @PathVariable int mes, @PathVariable int anio) {
        List<MovimientoDepreciacionDto> movimientos =
                depreciacionService.findByPeriodo(mes, anio);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Genera el reporte anual de depreciación.
     * Incluye todos los activos activos con sus valores y depreciaciones.
     */
    @GetMapping("/reporte-anual/{anio}")
    public ResponseEntity<List<ReporteDepreciacionDto>> generarReporteAnual(
            @PathVariable int anio) {
        List<ReporteDepreciacionDto> reporte = depreciacionService.generarReporteAnual(anio);
        return ResponseEntity.ok(reporte);
    }

    /**
     * Verifica si ya existe cierre de depreciación para un período.
     */
    @GetMapping("/existe-cierre/{mes}/{anio}")
    public ResponseEntity<Map<String, Boolean>> existeCierre(
            @PathVariable int mes, @PathVariable int anio) {
        boolean existe = depreciacionService.existeCierre(mes, anio);
        return ResponseEntity.ok(Map.of("existeCierre", existe));
    }
}
