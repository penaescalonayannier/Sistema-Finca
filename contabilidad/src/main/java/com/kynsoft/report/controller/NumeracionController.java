package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.EstadoConsecutivoDocumentoDto;
import com.kynsoft.report.domain.services.INumeracionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/numeracion")
public class NumeracionController {
    private final INumeracionService service;

    public NumeracionController(INumeracionService service) {
        this.service = service;
    }

    /** Registro oficial de consecutivos: no expone ninguna operación de edición o reinicio. */
    @GetMapping
    public ResponseEntity<List<EstadoConsecutivoDocumentoDto>> consultar(
            @RequestParam UUID fincaId,
            @RequestParam(required = false) Integer anio) {
        return ResponseEntity.ok(service.obtenerEstadoDocumentos(
                fincaId, anio == null ? LocalDate.now().getYear() : anio));
    }
}
