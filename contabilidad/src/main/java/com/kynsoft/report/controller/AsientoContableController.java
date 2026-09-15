package com.kynsoft.report.controller;

import com.kynsoft.report.domain.dto.AsientoContableDto;
import com.kynsoft.report.domain.services.IContabilizacionAutomaticaService;
import com.kynsoft.report.infrastructure.entity.AsientoContable;
import com.kynsoft.report.infrastructure.repository.query.AsientoContableReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.LineaAsientoReadDataJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for Accounting Entries (Libro Diario).
 * Read-only - entries are generated automatically from stock movements.
 */
@RestController
@RequestMapping("/api/asiento-contable")
@RequiredArgsConstructor
public class AsientoContableController {

    private final IContabilizacionAutomaticaService contabilizacionService;
    private final AsientoContableReadDataJPARepository asientoRepository;
    private final LineaAsientoReadDataJPARepository lineaRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "fecha") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AsientoContable> data = asientoRepository.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("items", data.getContent().stream()
                .map(AsientoContable::toAggregate)
                .collect(Collectors.toList()));
        response.put("totalElements", data.getTotalElements());
        response.put("totalPages", data.getTotalPages());
        response.put("currentPage", data.getNumber());
        response.put("pageSize", data.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsientoContableDto> findById(@PathVariable UUID id) {
        return asientoRepository.findByIdWithLineas(id)
                .map(a -> ResponseEntity.ok(a.toAggregate()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<AsientoContableDto> findByNumero(@PathVariable String numero) {
        return asientoRepository.findByNumero(numero)
                .map(a -> ResponseEntity.ok(a.toAggregate()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/movimiento/{movimientoStockId}")
    public ResponseEntity<AsientoContableDto> findByMovimientoStockId(@PathVariable UUID movimientoStockId) {
        return contabilizacionService.findAsientoByMovimientoStockId(movimientoStockId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/almacen/{almacenId}")
    public ResponseEntity<List<AsientoContableDto>> findByAlmacenId(
            @PathVariable UUID almacenId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<AsientoContable> asientos;
        if (fechaInicio != null && fechaFin != null) {
            asientos = asientoRepository.findByAlmacenIdAndFechaBetween(almacenId, fechaInicio, fechaFin);
        } else {
            asientos = asientoRepository.findByAlmacenId(almacenId);
        }

        return ResponseEntity.ok(asientos.stream()
                .map(AsientoContable::toAggregate)
                .collect(Collectors.toList()));
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<AsientoContableDto>> findByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(contabilizacionService.findAsientosByFecha(fechaInicio, fechaFin));
    }

    @GetMapping("/descuadrados")
    public ResponseEntity<List<AsientoContableDto>> findDescuadrados() {
        return ResponseEntity.ok(contabilizacionService.findAsientosDescuadrados());
    }

    @GetMapping("/totales")
    public ResponseEntity<Map<String, BigDecimal>> getTotalesPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        BigDecimal totalDebito = contabilizacionService.getTotalDebitoPorPeriodo(fechaInicio, fechaFin);
        BigDecimal totalCredito = contabilizacionService.getTotalCreditoPorPeriodo(fechaInicio, fechaFin);

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("totalDebito", totalDebito);
        result.put("totalCredito", totalCredito);
        result.put("diferencia", totalDebito.subtract(totalCredito));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/mayor/{codigoCuenta}")
    public ResponseEntity<Map<String, Object>> getMayorPorCuenta(
            @PathVariable String codigoCuenta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        var lineas = lineaRepository.findByCodigoCuentaAndPeriodo(codigoCuenta, fechaInicio, fechaFin);
        BigDecimal totalDebe = lineaRepository.sumDebePorCuentaYPeriodo(codigoCuenta, fechaInicio, fechaFin);
        BigDecimal totalHaber = lineaRepository.sumHaberPorCuentaYPeriodo(codigoCuenta, fechaInicio, fechaFin);
        BigDecimal saldo = lineaRepository.calcularSaldoCuenta(codigoCuenta);

        Map<String, Object> result = new HashMap<>();
        result.put("codigoCuenta", codigoCuenta);
        result.put("fechaInicio", fechaInicio);
        result.put("fechaFin", fechaFin);
        result.put("movimientos", lineas.stream()
                .map(l -> {
                    Map<String, Object> mov = new HashMap<>();
                    mov.put("id", l.getId());
                    mov.put("fecha", l.getAsiento().getFecha());
                    mov.put("numeroAsiento", l.getAsiento().getNumero());
                    mov.put("concepto", l.getConcepto());
                    mov.put("debe", l.getDebe());
                    mov.put("haber", l.getHaber());
                    mov.put("centroCosto", l.getCentroCosto());
                    return mov;
                })
                .collect(Collectors.toList()));
        result.put("totalDebe", totalDebe != null ? totalDebe : BigDecimal.ZERO);
        result.put("totalHaber", totalHaber != null ? totalHaber : BigDecimal.ZERO);
        result.put("saldo", saldo != null ? saldo : BigDecimal.ZERO);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/balance-comprobacion")
    public ResponseEntity<Map<String, Object>> getBalanceComprobacion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<String> cuentasConMovimiento = lineaRepository.findCuentasConMovimientoEnPeriodo(fechaInicio, fechaFin);

        List<Map<String, Object>> balances = cuentasConMovimiento.stream()
                .map(codigo -> {
                    BigDecimal debe = lineaRepository.sumDebePorCuentaYPeriodo(codigo, fechaInicio, fechaFin);
                    BigDecimal haber = lineaRepository.sumHaberPorCuentaYPeriodo(codigo, fechaInicio, fechaFin);
                    BigDecimal saldo = lineaRepository.calcularSaldoCuenta(codigo);

                    Map<String, Object> balance = new HashMap<>();
                    balance.put("codigoCuenta", codigo);
                    balance.put("debe", debe != null ? debe : BigDecimal.ZERO);
                    balance.put("haber", haber != null ? haber : BigDecimal.ZERO);
                    balance.put("saldoDeudor", saldo != null && saldo.compareTo(BigDecimal.ZERO) > 0 ? saldo : BigDecimal.ZERO);
                    balance.put("saldoAcreedor", saldo != null && saldo.compareTo(BigDecimal.ZERO) < 0 ? saldo.abs() : BigDecimal.ZERO);
                    return balance;
                })
                .collect(Collectors.toList());

        BigDecimal totalDebe = balances.stream()
                .map(b -> (BigDecimal) b.get("debe"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHaber = balances.stream()
                .map(b -> (BigDecimal) b.get("haber"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("fechaInicio", fechaInicio);
        result.put("fechaFin", fechaFin);
        result.put("cuentas", balances);
        result.put("totalDebe", totalDebe);
        result.put("totalHaber", totalHaber);
        result.put("cuadrado", totalDebe.compareTo(totalHaber) == 0);

        return ResponseEntity.ok(result);
    }
}
