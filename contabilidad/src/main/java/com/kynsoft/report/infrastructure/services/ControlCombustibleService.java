package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.ControlCombustibleDto;
import com.kynsoft.report.infrastructure.entity.ItemSalida;
import com.kynsoft.report.infrastructure.repository.query.ItemSalidaReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ControlCombustibleService {
    private final ItemSalidaReadDataJPARepository itemSalidaReadRepository;

    @Transactional(readOnly = true, transactionManager = "readTransactionManager")
    public ControlCombustibleDto consultar(UUID fincaId, LocalDate fechaInicio, LocalDate fechaFin) {
        TenantValidator.validateReadAccess(fincaId);
        LocalDate inicio = fechaInicio == null ? LocalDate.now().withDayOfMonth(1) : fechaInicio;
        LocalDate fin = fechaFin == null ? LocalDate.now() : fechaFin;
        if (fin.isBefore(inicio)) throw new IllegalArgumentException("La fecha final no puede ser anterior a la inicial.");

        Map<UUID, Acumulado> actividades = new LinkedHashMap<>();
        LocalDateTime acumuladoInicio = inicio.withDayOfYear(1).atStartOfDay();
        for (ItemSalida item : itemSalidaReadRepository.findConsumosCombustible(
                fincaId, acumuladoInicio, fin.plusDays(1).atStartOfDay())) {
            if (item.getLabor() == null) continue;
            Acumulado acumulado = actividades.computeIfAbsent(item.getLaborId(), id -> new Acumulado(item));
            acumulado.sumarAcumulado(item);
            if (!item.getSalida().getFecha().toLocalDate().isBefore(inicio)) acumulado.sumarPeriodo(item);
        }
        return ControlCombustibleDto.builder().fincaId(fincaId)
                .fechaInicio(inicio.toString()).fechaFin(fin.toString())
                .lineas(actividades.values().stream().map(Acumulado::dto)
                        .sorted(Comparator.comparing(ControlCombustibleDto.Linea::getActividad)).toList()).build();
    }

    private static class Acumulado {
        private final ItemSalida muestra;
        private double nivelPeriodo, consumoPeriodo, nivelAcumulado, consumoAcumulado;
        private Acumulado(ItemSalida muestra) { this.muestra = muestra; }
        private void sumarPeriodo(ItemSalida item) { nivelPeriodo += numero(item.getCantidadActividad()); consumoPeriodo += numero(item.getCantidad()); }
        private void sumarAcumulado(ItemSalida item) { nivelAcumulado += numero(item.getCantidadActividad()); consumoAcumulado += numero(item.getCantidad()); }
        private ControlCombustibleDto.Linea dto() {
            double indicePeriodo = nivelPeriodo == 0 ? 0 : consumoPeriodo / nivelPeriodo;
            double indiceAcumulado = nivelAcumulado == 0 ? 0 : consumoAcumulado / nivelAcumulado;
            Double permisible = muestra.getLabor().getIndiceConsumo() == null ? null : muestra.getLabor().getIndiceConsumo().doubleValue();
            return ControlCombustibleDto.Linea.builder().laborId(muestra.getLaborId())
                    .actividad(muestra.getLabor().getName()).unidadMedida(muestra.getSalida().getFincaProducto().getProducto().getUnidadMedida().name())
                    .nivelActividad(muestra.getLabor().getNivelActividad()).indicePermisible(permisible)
                    .nivelPeriodo(nivelPeriodo).consumoPeriodo(consumoPeriodo).indicePeriodo(indicePeriodo)
                    .nivelAcumulado(nivelAcumulado).consumoAcumulado(consumoAcumulado).indiceAcumulado(indiceAcumulado)
                    .desviacion(permisible == null ? null : indicePeriodo - permisible).build();
        }
        private static double numero(Double valor) { return valor == null ? 0 : valor; }
    }
}
