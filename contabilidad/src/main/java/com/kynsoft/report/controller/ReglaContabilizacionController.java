package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.ReglaContabilizacionDto;
import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.domain.services.IContabilizacionAutomaticaService;
import com.kynsoft.report.infrastructure.entity.ReglaContabilizacion;
import com.kynsoft.report.infrastructure.repository.query.ReglaContabilizacionReadDataJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for Accounting Rules (Reglas de Contabilización).
 * Allows viewing and managing the rules that map stock movements to accounts.
 */
@RestController
@RequestMapping("/api/regla-contabilizacion")
@RequiredArgsConstructor
public class ReglaContabilizacionController {

    private final IContabilizacionAutomaticaService contabilizacionService;
    private final ReglaContabilizacionReadDataJPARepository repository;

    @GetMapping
    public ResponseEntity<List<ReglaContabilizacionDto>> findAll() {
        return ResponseEntity.ok(contabilizacionService.findAllReglas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReglaContabilizacionDto> findById(@PathVariable UUID id) {
        return repository.findById(id)
                .map(r -> ResponseEntity.ok(r.toAggregate()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tipo/{tipoMovimiento}")
    public ResponseEntity<List<ReglaContabilizacionDto>> findByTipoMovimiento(
            @PathVariable TipoMovimientoStock tipoMovimiento) {
        return ResponseEntity.ok(contabilizacionService.findReglasByTipoMovimiento(tipoMovimiento));
    }

    @GetMapping("/tipos-movimiento")
    public ResponseEntity<List<TipoMovimientoStock>> getTiposMovimientoConRegla() {
        return ResponseEntity.ok(repository.findTiposMovimientoConRegla());
    }

    @GetMapping("/all-tipos-movimiento")
    public ResponseEntity<TipoMovimientoStock[]> getAllTiposMovimiento() {
        return ResponseEntity.ok(TipoMovimientoStock.values());
    }

    @PostMapping
    public ResponseEntity<ReglaContabilizacionDto> create(@RequestBody ReglaContabilizacionDto regla) {
        ReglaContabilizacionDto created = contabilizacionService.crearRegla(regla);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable UUID id) {
        contabilizacionService.activarRegla(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
        contabilizacionService.desactivarRegla(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/finca/{fincaId}")
    public ResponseEntity<List<ReglaContabilizacionDto>> findByFincaId(@PathVariable UUID fincaId) {
        return ResponseEntity.ok(repository.findByFincaIdAndActivoTrue(fincaId)
                .stream()
                .map(ReglaContabilizacion::toAggregate)
                .collect(Collectors.toList()));
    }

    @GetMapping("/almacen/{almacenId}")
    public ResponseEntity<List<ReglaContabilizacionDto>> findByAlmacenId(@PathVariable UUID almacenId) {
        return ResponseEntity.ok(repository.findByAlmacenIdAndActivoTrue(almacenId)
                .stream()
                .map(ReglaContabilizacion::toAggregate)
                .collect(Collectors.toList()));
    }
}
