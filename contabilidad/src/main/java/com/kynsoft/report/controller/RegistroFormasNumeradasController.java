package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.AnularEmisionFormaNumeradaRequest;
import com.kynsoft.report.domain.dto.EmisionFormaNumeradaDto;
import com.kynsoft.report.domain.dto.RegistroFormaNumeradaDto;
import com.kynsoft.report.domain.dto.ReimprimirFormaNumeradaRequest;
import com.kynsoft.report.domain.services.IRegistroFormasNumeradasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Consulta y control del libro de formas sin alterar el endpoint histórico /api/numeracion. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/numeracion/formas")
public class RegistroFormasNumeradasController {

    private final IRegistroFormasNumeradasService service;

    @GetMapping
    public ResponseEntity<List<RegistroFormaNumeradaDto>> consultar(
            @RequestParam(required = false) UUID fincaId,
            @RequestParam(required = false) Integer anio) {
        return ResponseEntity.ok(service.consultar(fincaId, anio == null ? LocalDate.now().getYear() : anio));
    }

    @GetMapping("/{codigoForma}/emisiones")
    public ResponseEntity<List<EmisionFormaNumeradaDto>> emisiones(
            @PathVariable String codigoForma,
            @RequestParam(required = false) UUID fincaId,
            @RequestParam(required = false) Integer anio) {
        return ResponseEntity.ok(service.consultarEmisiones(
                codigoForma, fincaId, anio == null ? LocalDate.now().getYear() : anio));
    }

    @PostMapping("/emisiones/{id}/anular")
    public ResponseEntity<EmisionFormaNumeradaDto> anular(
            @PathVariable UUID id, @RequestBody AnularEmisionFormaNumeradaRequest request) {
        return ResponseEntity.ok(service.anular(id, request));
    }

    @PostMapping("/emisiones/{id}/reimprimir")
    public ResponseEntity<EmisionFormaNumeradaDto> reimprimir(
            @PathVariable UUID id, @RequestBody(required = false) ReimprimirFormaNumeradaRequest request) {
        return ResponseEntity.ok(service.registrarReimpresion(id, request == null ? null : request.getUsuarioId()));
    }
}
