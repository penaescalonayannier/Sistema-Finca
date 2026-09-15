package com.kynsoft.report.controller;

import com.kynsoft.report.controller.request.CuentaContableRequest;
import com.kynsoft.report.domain.dto.CuentaContableDto;
import com.kynsoft.report.domain.dto.TipoCuenta;
import com.kynsoft.report.domain.services.ICuentaContableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for Chart of Accounts (Plan de Cuentas).
 * Supports full CRUD for cost centers and accounts.
 */
@RestController
@RequestMapping("/api/cuenta-contable")
@RequiredArgsConstructor
public class CuentaContableController {

    private final ICuentaContableService service;

    // ==================== CRUD ====================

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody CuentaContableRequest request) {
        CuentaContableDto dto = CuentaContableDto.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .tipo(request.getTipo())
                .naturaleza(request.getNaturaleza())
                .nivel(request.getNivel())
                .cuentaPadreId(request.getCuentaPadreId())
                .permiteMovimiento(request.getPermiteMovimiento())
                .esCentroCosto(request.getEsCentroCosto())
                .activo(request.getActivo())
                .build();
        UUID id = service.create(dto);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody CuentaContableRequest request) {
        CuentaContableDto dto = CuentaContableDto.builder()
                .id(id)
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .tipo(request.getTipo())
                .naturaleza(request.getNaturaleza())
                .nivel(request.getNivel())
                .cuentaPadreId(request.getCuentaPadreId())
                .permiteMovimiento(request.getPermiteMovimiento())
                .esCentroCosto(request.getEsCentroCosto())
                .activo(request.getActivo())
                .build();
        service.update(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable UUID id) {
        service.activar(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
        service.desactivar(id);
        return ResponseEntity.ok().build();
    }

    // ==================== CONSULTAS ====================

    @GetMapping
    public ResponseEntity<List<CuentaContableDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaContableDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<CuentaContableDto> findByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(service.findByCodigo(codigo));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<CuentaContableDto>> findByTipo(@PathVariable TipoCuenta tipo) {
        return ResponseEntity.ok(service.findByTipo(tipo));
    }

    @GetMapping("/grupos-principales")
    public ResponseEntity<List<CuentaContableDto>> findGruposPrincipales() {
        return ResponseEntity.ok(service.findGruposPrincipales());
    }

    @GetMapping("/movibles")
    public ResponseEntity<List<CuentaContableDto>> findCuentasMovibles() {
        return ResponseEntity.ok(service.findCuentasMovibles());
    }

    @GetMapping("/centros-costo")
    public ResponseEntity<List<CuentaContableDto>> findCentrosCosto() {
        return ResponseEntity.ok(service.findCentrosCosto());
    }

    @GetMapping("/subcuentas/{cuentaPadreId}")
    public ResponseEntity<List<CuentaContableDto>> findSubcuentas(@PathVariable UUID cuentaPadreId) {
        return ResponseEntity.ok(service.findSubcuentas(cuentaPadreId));
    }

    @GetMapping("/jerarquia/{codigoPadre}")
    public ResponseEntity<List<CuentaContableDto>> getJerarquiaCompleta(@PathVariable String codigoPadre) {
        return ResponseEntity.ok(service.getJerarquiaCompleta(codigoPadre));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CuentaContableDto>> search(@RequestParam String query) {
        return ResponseEntity.ok(service.search(query));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countActivas() {
        return ResponseEntity.ok(service.countActivas());
    }
}
