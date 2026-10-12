package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.ControlCombustibleDto;
import com.kynsoft.report.infrastructure.services.ControlCombustibleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/control-combustible")
public class ControlCombustibleController {
    private final ControlCombustibleService service;

    @GetMapping
    public ResponseEntity<ControlCombustibleDto> consultar(@RequestParam UUID fincaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(service.consultar(fincaId, fechaInicio, fechaFin));
    }
}
