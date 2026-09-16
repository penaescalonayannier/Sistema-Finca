package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.EntregaBancoRequest;
import com.kynsoft.report.domain.dto.LiquidarSalidaRequest;
import com.kynsoft.report.domain.dto.SaldoCajaDto;
import com.kynsoft.report.domain.dto.SalidaPendienteLiquidacionDto;
import com.kynsoft.report.domain.services.ILiquidacionSalidaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/liquidaciones-salida")
public class LiquidacionSalidaController {
    private final ILiquidacionSalidaService service;

    public LiquidacionSalidaController(ILiquidacionSalidaService service) { this.service = service; }

    @GetMapping("/pendientes")
    public ResponseEntity<List<SalidaPendienteLiquidacionDto>> pendientes(
            @RequestParam(required = false) UUID fincaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(service.pendientes(fincaId, fechaInicio, fechaFin));
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> liquidar(@RequestBody LiquidarSalidaRequest request) {
        return ResponseEntity.ok(Map.of("id", service.liquidar(request)));
    }

    @GetMapping("/caja/saldo")
    public ResponseEntity<SaldoCajaDto> saldoCaja(@RequestParam UUID fincaId) {
        return ResponseEntity.ok(service.obtenerSaldoCaja(fincaId));
    }

    @PostMapping("/caja/entregas-banco")
    public ResponseEntity<Map<String, UUID>> entregarBanco(@RequestBody EntregaBancoRequest request) {
        return ResponseEntity.ok(Map.of("id", service.entregarBanco(request)));
    }
}
