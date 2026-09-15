package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.MovimientoDepreciacionDto;
import com.kynsoft.report.domain.services.IDepreciacionService;
import com.kynsoft.report.infrastructure.entity.ActivoFijoTangible;
import com.kynsoft.report.infrastructure.entity.GrupoActivoFijo;
import com.kynsoft.report.infrastructure.entity.MovimientoDepreciacion;
import com.kynsoft.report.infrastructure.repository.command.ActivoFijoTangibleWriteRepository;
import com.kynsoft.report.infrastructure.repository.command.MovimientoDepreciacionWriteRepository;
import com.kynsoft.report.infrastructure.repository.query.ActivoFijoTangibleReadRepository;
import com.kynsoft.report.infrastructure.repository.query.MovimientoDepreciacionReadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Depreciación.
 *
 * Referencia normativa:
 * - NCC No. 7 (Resolución 1038/2017 MFP): Método de depreciación línea recta
 * - Resolución 51/2021 MFP: Tasas máximas de depreciación
 *
 * Fórmula según NCC No. 7:
 * Depreciación Anual = Valor Adquisición × Tasa%
 * Depreciación Mensual = Depreciación Anual / 12
 */
@Service
public class DepreciacionServiceImpl implements IDepreciacionService {

    private final ActivoFijoTangibleReadRepository activoFijoReadRepository;
    private final ActivoFijoTangibleWriteRepository activoFijoWriteRepository;
    private final MovimientoDepreciacionReadRepository movimientoReadRepository;
    private final MovimientoDepreciacionWriteRepository movimientoWriteRepository;

    public DepreciacionServiceImpl(ActivoFijoTangibleReadRepository activoFijoReadRepository,
                                    ActivoFijoTangibleWriteRepository activoFijoWriteRepository,
                                    MovimientoDepreciacionReadRepository movimientoReadRepository,
                                    MovimientoDepreciacionWriteRepository movimientoWriteRepository) {
        this.activoFijoReadRepository = activoFijoReadRepository;
        this.activoFijoWriteRepository = activoFijoWriteRepository;
        this.movimientoReadRepository = movimientoReadRepository;
        this.movimientoWriteRepository = movimientoWriteRepository;
    }

    @Override
    public BigDecimal calcularDepreciacionMensual(UUID activoFijoId) {
        ActivoFijoTangible activo = activoFijoReadRepository.findById(activoFijoId)
                .orElseThrow(() -> new RuntimeException("Activo no encontrado: " + activoFijoId));

        return calcularDepreciacionMensual(activo);
    }

    /**
     * Calcula la depreciación mensual según NCC No. 7.
     * Depreciación Mensual = (Valor Adquisición × Tasa Anual%) / 12
     */
    private BigDecimal calcularDepreciacionMensual(ActivoFijoTangible activo) {
        GrupoActivoFijo grupo = activo.getGrupo();
        if (grupo == null || grupo.getTasaDepreciacion() == null) {
            return BigDecimal.ZERO;
        }

        // Si ya está totalmente depreciado, no depreciar más
        if (activo.getValorResidual() != null &&
            activo.getValorResidual().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal valorAdquisicion = activo.getValorAdquisicion();
        BigDecimal tasaAnual = grupo.getTasaDepreciacion();

        // Depreciación anual = Valor × Tasa%
        BigDecimal depreciacionAnual = valorAdquisicion
                .multiply(tasaAnual)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Depreciación mensual = Depreciación anual / 12
        BigDecimal depreciacionMensual = depreciacionAnual
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        // No depreciar más allá del valor residual
        BigDecimal valorResidualActual = activo.getValorAdquisicion()
                .subtract(activo.getDepreciacionAcumulada() != null ?
                        activo.getDepreciacionAcumulada() : BigDecimal.ZERO);

        if (depreciacionMensual.compareTo(valorResidualActual) > 0) {
            depreciacionMensual = valorResidualActual;
        }

        return depreciacionMensual;
    }

    @Override
    @Transactional
    public List<MovimientoDepreciacionDto> ejecutarCierreMensual(int mes, int anio) {
        if (existeCierre(mes, anio)) {
            throw new RuntimeException("Ya existe cierre de depreciación para " + mes + "/" + anio);
        }

        List<ActivoFijoTangible> activos = activoFijoReadRepository.findByActivoTrueAndFechaBajaIsNull();
        List<MovimientoDepreciacionDto> movimientos = new ArrayList<>();

        for (ActivoFijoTangible activo : activos) {
            MovimientoDepreciacionDto mov = ejecutarCierreActivo(activo.getId(), mes, anio);
            if (mov != null && mov.getMontoDepreciacion().compareTo(BigDecimal.ZERO) > 0) {
                movimientos.add(mov);
            }
        }

        return movimientos;
    }

    @Override
    @Transactional
    public MovimientoDepreciacionDto ejecutarCierreActivo(UUID activoFijoId, int mes, int anio) {
        // Verificar si ya existe movimiento para este activo en este período
        List<MovimientoDepreciacion> existentes = movimientoReadRepository
                .findByActivoFijoIdAndPeriodo(activoFijoId, mes, anio);
        if (!existentes.isEmpty()) {
            return existentes.get(0).toAggregate();
        }

        ActivoFijoTangible activo = activoFijoReadRepository.findById(activoFijoId)
                .orElseThrow(() -> new RuntimeException("Activo no encontrado"));

        BigDecimal depreciacionMensual = calcularDepreciacionMensual(activo);

        if (depreciacionMensual.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        BigDecimal depreciacionAnterior = activo.getDepreciacionAcumulada() != null ?
                activo.getDepreciacionAcumulada() : BigDecimal.ZERO;
        BigDecimal depreciacionNueva = depreciacionAnterior.add(depreciacionMensual);
        BigDecimal valorResidualNuevo = activo.getValorAdquisicion().subtract(depreciacionNueva);

        // Crear movimiento
        MovimientoDepreciacion movimiento = new MovimientoDepreciacion();
        movimiento.setId(UUID.randomUUID());
        movimiento.setActivoFijo(activo);
        movimiento.setFecha(LocalDate.of(anio, mes, 1));
        movimiento.setMes(mes);
        movimiento.setAnio(anio);
        movimiento.setMontoDepreciacion(depreciacionMensual);
        movimiento.setDepreciacionAcumuladaAnterior(depreciacionAnterior);
        movimiento.setDepreciacionAcumuladaNueva(depreciacionNueva);
        movimiento.setValorResidualResultante(valorResidualNuevo);
        movimiento.setTasaAplicada(activo.getGrupo() != null ?
                activo.getGrupo().getTasaDepreciacion() : null);
        movimiento.setObservacion("Cierre mensual " + mes + "/" + anio +
                " según NCC No. 7 (Res. 1038/2017 MFP)");

        movimiento = movimientoWriteRepository.save(movimiento);

        // Actualizar activo fijo
        activo.setDepreciacionAcumulada(depreciacionNueva);
        activo.setValorResidual(valorResidualNuevo);
        activoFijoWriteRepository.save(activo);

        return movimiento.toAggregate();
    }

    @Override
    public List<MovimientoDepreciacionDto> findByActivoFijo(UUID activoFijoId) {
        return movimientoReadRepository.findByActivoFijoIdOrderByAnioDescMesDesc(activoFijoId).stream()
                .map(MovimientoDepreciacion::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoDepreciacionDto> findByPeriodo(int mes, int anio) {
        return movimientoReadRepository.findByMesAndAnioOrderByActivoFijoNumeroInventarioAsc(mes, anio).stream()
                .map(MovimientoDepreciacion::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReporteDepreciacionDto> generarReporteAnual(int anio) {
        List<ActivoFijoTangible> activos = activoFijoReadRepository.findByActivoTrueAndFechaBajaIsNull();
        List<ReporteDepreciacionDto> reporte = new ArrayList<>();

        for (ActivoFijoTangible activo : activos) {
            // Calcular depreciación anual
            BigDecimal tasaAnual = activo.getGrupo() != null && activo.getGrupo().getTasaDepreciacion() != null ?
                    activo.getGrupo().getTasaDepreciacion() : BigDecimal.ZERO;

            BigDecimal depreciacionAnual = activo.getValorAdquisicion()
                    .multiply(tasaAnual)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            reporte.add(new ReporteDepreciacionDto(
                    activo.getId(),
                    activo.getNumeroInventario(),
                    activo.getDescripcion(),
                    activo.getGrupo() != null ? activo.getGrupo().getCodigo() : null,
                    activo.getValorAdquisicion(),
                    depreciacionAnual,
                    activo.getDepreciacionAcumulada(),
                    activo.getValorResidual(),
                    tasaAnual
            ));
        }

        return reporte;
    }

    @Override
    public boolean existeCierre(int mes, int anio) {
        return movimientoReadRepository.existsByMesAndAnio(mes, anio);
    }
}
